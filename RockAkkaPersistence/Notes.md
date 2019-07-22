# Akka Persistence

## Akka Persistence Primer

Persistent actors can do everything a normal actor can do, but they also:
- have a persistent ID
- can persist events
- can recover its state from the store (event sourcing)

Persistent actors will extend `kka.persistence.PersistentActor`, and must implement the `receiveCommand`, `receiveRecover` and `persistentId` methods.

When a persistent actor is being recovered, all incoming messages are stashed.

You are not forced to always act as a persistent actor: certain commands can act just like a normal actor, without persistence.

### Failures

Persistence can sometimes fail, which makes the actor stop. Strategies such as Backoff supervisor can start the actor again after a while.
You can listen to this event by overriding the `onPersistFailure` method.

Persistence can also fail if the journal throws an exception. In this case, the actor is resumed. You can listen to this event by overriding the `onPersistReject` method.

Never call `persist` or `persistAll` from `Future`s: it will break the actor encapsulation.

### Shutdown of persistent actors

If you shutdown the actor with `PoisionPill`, the messages will never be delivered. The best way to handle shutdown is to write your own implementation.

### Multiple persistence

- Persistence is based on messages.
- Calls to `persist()` are executed **in order**.
- Handlers for subsequent `persist()` calls are executed **in order**.

### Snapshots

Snapshots let you store the entire state of something for faster recovery.
- `saveSnapshot` lets you save snapshots.
- The `SnapshotOffer(metadata, contents)` command will load the snapshot.
- Best practice: handle `SaveSnapshotSuccess` and `SaveSnapshotFailure` commandas.

### Recovery

When recovering an actor, if the recovery fails `onRecoveryFailure` will be called.

The method `recovery` is used to customize the recovery process.

The method `recoveryFinished` will tell you if recovery has been finished yet or not. 

To recover stateless actors, use `context.become` as normal.

`persistAsync` is useful for high-throughput environments. It is not good when you depend on state mutation because of possible inconsistencies.