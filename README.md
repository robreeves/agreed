# Agreed

# Distributed Lock Sample App

TODO summary

## CLI

```
Usage: com.robertpreeves.lock.Run [options]
  Options:
    -agreed-port
      The port for internal agreed communication
    -dir
      The directory where the files for distributed access are located. This 
      should be a path that all nodes can access.
    -help
      Documentation for this CLI.
    -nodes
      The list of other nodes in the group in the format 
      hostname:port,hostname:port,... 
    -port
      The port for the public REST API
```

## RESTful API

### `GET /api/file/<fileName>`

Gets the current contents of the file.

### `POST /api/file/<fileName>/<content>`

Appends a file with the request content.
