#!/bin/bash

set -euo pipefail

# S3: Simple Storage Service

# Local variables
ENDPOINT_URL="http://localhost:4566" # is the default endpoint for MiniStack
REGION="${AWS_REGION:-us-east-1}"
STACK_NAME="patient-management"
TEMPLATE_FILE="./cdk.out/MiniStack.template.json"
ARTIFACT_BUCKET="patient-management-cfn-artifacts"

# Environment variables
# MiniStack accepts any access key id/secret pair and
# does not validate them against a real IAM store.
export AWS_ACCESS_KEY_ID="test"
export AWS_SECRET_ACCESS_KEY="test"
export AWS_DEFAULT_REGION="$REGION"
# Disable the pager (is used to display long output in a scrollable view)
# for AWS CLI commands to avoid issues in non-interactive environments.
export AWS_PAGER=""
# The `aws cloudformation deploy` command creates an internal S3 client that 
# does NOT inherit the `--endpoint-url` flag.
export AWS_ENDPOINT_URL="$ENDPOINT_URL"
export AWS_ENDPOINT_URL_S3="$ENDPOINT_URL"
# AWS CLI 2.36+ defaults to the CRC64NVME checksum for S3 uploads, which
# MiniStack does not support.
# Compute a checksum when the service required (S3 never does for PutObject).
export AWS_REQUEST_CHECKSUM_CALCULATION="when_required"

# head-bucket: check if the bucket exists and has permission to access it
# 0 = stdin (standard input), 1 = stdout (standard output), 2 = stderr (standard error)
# >/dev/null: discard all standard output
# 2>&1: redirect stderr to where stdout is headed (to /dev/null)
# if the bucket does not exist (!0 = 1), create it.
if ! aws --endpoint-url="$ENDPOINT_URL" --region "$REGION" s3api head-bucket --bucket "$ARTIFACT_BUCKET" >/dev/null 2>&1; then
    echo "Creating artifact bucket: $ARTIFACT_BUCKET"
    aws --endpoint-url="$ENDPOINT_URL" --region "$REGION" s3api create-bucket \
        --bucket "$ARTIFACT_BUCKET" >/dev/null
fi

# MiniStack accepts templates <= 51KB in `TEMPLATE_FILE` to
# deploy the CloudFormation template directory. 
aws --endpoint-url="$ENDPOINT_URL" --region "$REGION" cloudformation deploy \
    --stack-name "$STACK_NAME" \
    --template-file "$TEMPLATE_FILE" \
    --no-fail-on-empty-changeset

aws --endpoint-url="$ENDPOINT_URL" --region "$REGION" cloudformation describe-stacks \
    --stack-name "$STACK_NAME" \
    --query "Stacks[0].StackStatus" \
    --output text