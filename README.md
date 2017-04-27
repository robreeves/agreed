# Agreed

# Distributed Lock Sample App

TODO summary

## CLI

```
Usage: com.robertpreeves.lock.Run [options]
  Options:
    -dir
      The directory where the files for distributed access are located. This 
      should be a path that all nodes can access.
    -help
      Documentation for this CLI.
    -node-index
      The index for this node in the -nodes file.
    -nodes
      The path to the file with the list of nodes.
    -port
      The port for the public REST API
```

## RESTful API

### `GET /api/file/<fileName>`

Gets the current contents of the file.

### `POST /api/file/<fileName>/<content>`

Appends a file with the request content.
