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
kubectl delete job install-apk -n student-fitness --ignore-not-found
kubectl apply -f kubernetes/install-job.yaml
kubectl logs -n student-fitness job/install-apk -f
```

> **Important:** Always delete the old `install-apk` Job before re-applying. Kubernetes Jobs are immutable once created — if the Job already exists, `kubectl apply` will report `unchanged` and no new pod will run. The `--ignore-not-found` flag makes the delete safe to run even when no prior Job exists, and works on Windows, macOS, and Linux.

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
- `emulator-data-pvc` persists emulator AVD data (`/home/androidusr/.android`) across pod restarts.
  > **Note:** Unlike Docker named volumes, Kubernetes PVCs start empty and do **not** inherit the container's existing directory contents on first use. For this reason the PVC is mounted at `/home/androidusr/.android` (the AVD state directory) rather than the top-level `/home/androidusr`, which would shadow the startup scripts shipped inside the image and cause a `CrashLoopBackOff`. If you previously applied `emulator.yaml` with the old mount path, delete and recreate the `emulator-data-pvc` PVC and the emulator Deployment before re-applying:
  > ```bash
  > kubectl delete deployment emulator -n student-fitness
  > kubectl delete pvc emulator-data-pvc -n student-fitness
  > kubectl apply -f kubernetes/persistent-volume-claims.yaml
  > kubectl apply -f kubernetes/emulator.yaml
  > ```
- If you need to re-run Jobs, delete the previous Job resource first:

```bash
kubectl delete job build-apk run-unit-tests install-apk -n student-fitness --ignore-not-found
```

### `kubectl apply` says `unchanged` and the app is not installed

This means the `install-apk` Job already existed from a previous run. Kubernetes Jobs are immutable — `kubectl apply` will not create a new pod if the Job spec has not changed.

**What happened:** The old job's logs are shown by `kubectl logs`, making it appear the install succeeded. But if the emulator pod was restarted since that old run, the app is no longer installed.

**Fix:** Delete the old job first, then re-apply:

```bat
kubectl delete job install-apk -n student-fitness --ignore-not-found
kubectl apply -f kubernetes/install-job.yaml
kubectl logs -n student-fitness job/install-apk -f
```

> **Windows note:** Do not use `2>/dev/null || true` — that is bash syntax and does not work in Command Prompt or PowerShell. Use `--ignore-not-found` instead, which is a native `kubectl` flag and works on all platforms.
