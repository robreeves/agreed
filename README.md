# Summary

Agreed is a general purpose consensus library based on basic Paxos. The API exposes methods to 
propose a new value and get the most recent consensus value.

# API

```java
import com.robertpreeves.agreed.*;

// Create a node
AgreedNode<T> node = AgreedNodeFactory.create(localNodeId, localNodePort, otherNodes);

// Propose a value
T acceptedValue = node.propose(newValue);

// Get the current value
T currentValue = node.current();  
```

# Logging

todo. reminder to describe logging.

# Disclaimer

This is an academic exercise to learn the basics of Paxos. There are many things that could be improved. Performance could be optimized, the service could be hardened (i.e. handle durable state corruption), etc. For a production 
application there is also the small detail of writing tests...
