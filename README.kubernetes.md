# Kubernetes Setup — Student Fitness App

This setup mirrors the existing Docker Compose workflow with Kubernetes resources for:

- APK build (`build-apk` Job)
- Unit tests (`run-unit-tests` Job)
- Android emulator (`emulator` Deployment + Service)
- APK install to emulator (`install-apk` Job)

> `build-apk`, `run-unit-tests`, and `install-apk` share a `ReadWriteOnce` PVC and should be run sequentially (not at the same time).

## Prerequisites

- A Kubernetes cluster with support for privileged pods
- A node that exposes `/dev/kvm` (required by the Android emulator)
- `kubectl` configured for your cluster
- Docker image for this project built from the existing `Dockerfile`

## 1) Build the app image

From the repository root:

```bash
docker build -t student-fitness-android:local .
```

If using a local cluster, load that image into the cluster runtime.

**minikube:**
```bash
minikube image load student-fitness-android:local
```

**kind** (Kubernetes in Docker):

First, make sure the `kind` CLI is installed. If it is not:
- **Windows:** Download the binary from https://kind.sigs.k8s.io/dl/latest/kind-windows-amd64 and place it in a directory on your `PATH` (e.g. `C:\Windows\System32\kind.exe`), or install via Chocolatey: `choco install kind`
- **macOS:** `brew install kind`
- **Linux:** `go install sigs.k8s.io/kind@latest` or download from the releases page

Then load the image into all kind nodes. If your cluster is the default unnamed cluster:
```bash
kind load docker-image student-fitness-android:local
```

If your cluster has a name (check with `kind get clusters`):
```bash
kind load docker-image student-fitness-android:local --name <cluster-name>
```

> **Note:** `kind load docker-image` must be re-run every time you rebuild the image, because kind nodes have their own isolated container runtime separate from your local Docker daemon.

## 2) Apply Kubernetes resources

```bash
kubectl apply -f kubernetes/namespace.yaml
kubectl apply -f kubernetes/persistent-volume-claims.yaml
kubectl apply -f kubernetes/emulator.yaml
```

## 3) Build the APK

```bash
kubectl apply -f kubernetes/build-job.yaml
kubectl logs -n student-fitness job/build-apk -f
```

## 4) Run unit tests

```bash
kubectl apply -f kubernetes/test-job.yaml
kubectl logs -n student-fitness job/run-unit-tests -f
```

## 5) Install APK on the emulator

After the emulator is running and the build job has completed:

```bash
kubectl apply -f kubernetes/install-job.yaml
kubectl logs -n student-fitness job/install-apk -f
```

## Accessing the emulator UI

The emulator service is exposed as `NodePort`. To discover the allocated Web VNC port:

```bash
kubectl get svc emulator -n student-fitness
```

Open `http://<node-ip>:<node-port>` for the `6080/TCP` service port.

## Troubleshooting

### `ERROR: no nodes found for cluster "kind"` (or any cluster name)

This error means no kind cluster is currently running. kind clusters are **ephemeral** — they do not persist across machine reboots or Docker Desktop restarts. You must create the cluster before loading images or deploying resources.

**1. Create a kind cluster**

```bat
kind create cluster --name kind
```

Wait ~1–2 minutes for the cluster to be ready.

**2. Verify it is running**

```bat
kind get clusters
kubectl cluster-info --context kind-kind
```

**3. Load your image into the cluster**

```bat
kind load docker-image student-fitness-android:local --name kind
```

**4. Continue with deployment**

```bat
kubectl apply -f kubernetes/
```

> **Why this happens:** Unlike Docker containers, a kind cluster lives inside Docker containers that are destroyed when Docker Desktop stops or your machine restarts. You need to re-run `kind create cluster` and `kind load docker-image` each time this happens.

---

## Notes

- `build-artifacts-pvc` stores Gradle build outputs and reports shared across Jobs.
- `build-artifacts-pvc` uses `ReadWriteOnce` for broad storage-class compatibility, so run the Jobs one at a time.
- `emulator-data-pvc` persists emulator data (`/home/androidusr`) across pod restarts.
- If you need to re-run Jobs, delete the previous Job resource first:

```bash
kubectl delete job build-apk run-unit-tests install-apk -n student-fitness
```
