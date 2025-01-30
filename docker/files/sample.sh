#!/usr/bin/env bash

set -euo pipefail

host="localhost"
port="8899"
args=("$@")

if [[ $# -gt 0 ]] && [[ "$1" == "client" ]]; then
  prev=""
  for arg in "$@"; do
    case "${prev}" in
    "--host")
      host="${arg}"
      ;;
    "--port")
      port="${arg}"
      ;;
    esac
    prev="${arg}"
  done
  ./wait-for-it.sh -h "${host}" -p "${port}"
fi

java -jar /app/sample.jar "${args[@]}"
