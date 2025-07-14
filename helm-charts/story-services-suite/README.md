## MySQL Configuration

Command to create secret it. This command does not apply it directly to the cluster. It just generates the file that 
we'll encode for storage in Git.

```bash
kubectl create secret generic mysql-auth \
--from-literal=mysql-root-password='rootpass' \
--from-literal=mysql-password='secretpass' \
--from-literal=mysql-replication-password=dummy \
--dry-run=client -o yaml > mysql-secret.yaml
```

Command to apply the secret, so it is in the cluster.

```bash
kubectl apply -f mysql-secret.yaml
```

## AI Story Creation Service Configuration

If we want to use OpenAI, we need to create a secret with the API key.

```bash
kubectl create secret generic openai-api-key \
--from-literal=api-key='sk-dummykey-1234567890abcdef1234567890abcdef' \
--dry-run=client -o yaml > openai-secret.yaml
```

It is applied the same way as above secret.