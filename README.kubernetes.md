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

If using a local cluster, load that image into the cluster runtime (example for minikube):

```bash
minikube image load student-fitness-android:local
```

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

## Notes

- `build-artifacts-pvc` stores Gradle build outputs and reports shared across Jobs.
- `build-artifacts-pvc` uses `ReadWriteOnce` for broad storage-class compatibility, so run the Jobs one at a time.
- `emulator-data-pvc` persists emulator data (`/home/androidusr`) across pod restarts.
- If you need to re-run Jobs, delete the previous Job resource first:

```bash
kubectl delete job build-apk run-unit-tests install-apk -n student-fitness
```
