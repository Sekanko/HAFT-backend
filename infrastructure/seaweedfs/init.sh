#!/bin/sh
set -eu

EP="${S3_ENDPOINT:-http://seaweedfs:8333}"
s3api() { aws --endpoint-url "$EP" s3api "$@"; }

echo "Waiting for SeaweedFS at $EP..."
until out=$(s3api list-buckets 2>&1); do
  case "$out" in
    *"Could not connect"*|*"Connection refused"*|*"Name or service not known"*|*"Temporary failure in name resolution"*|*"timed out"*)
      echo "Waiting for SeaweedFS..."
      sleep 3
      ;;
    *)
      echo "$out" >&2
      exit 1
      ;;
  esac
done

for b in "$PUBLIC_BUCKET" "$PRIVATE_BUCKET"; do
  if ! out=$(s3api create-bucket --bucket "$b" 2>&1); then
    case "$out" in
      *"BucketAlreadyExists"*|*"BucketAlreadyOwnedByYou"*)
        ;;
      *)
        echo "$out" >&2
        exit 1
        ;;
    esac
  fi
done

render() {
  sed -e "s|__BUCKET__|$2|g" -e "s|__ACCESS_KEY__|$AWS_ACCESS_KEY_ID|g" "$1"
}

render /init/policies/public.json.tpl  "$PUBLIC_BUCKET"  > /tmp/public.json
render /init/policies/private.json.tpl "$PRIVATE_BUCKET" > /tmp/private.json

s3api put-bucket-policy --bucket "$PUBLIC_BUCKET"  --policy file:///tmp/public.json
s3api put-bucket-policy --bucket "$PRIVATE_BUCKET" --policy file:///tmp/private.json

echo "S3 init completed"