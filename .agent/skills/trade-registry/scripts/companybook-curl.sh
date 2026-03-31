#!/bin/bash

if [ -z "$COMPANYBOOK_API_KEY" ]; then
  echo "Error: COMPANYBOOK_API_KEY environment variable is not set" >&2
  exit 1
fi
curl -s -H "X-API-Key: $COMPANYBOOK_API_KEY" "$@"