FROM ubuntu:latest
LABEL authors="Red T"

ENTRYPOINT ["top", "-b"]