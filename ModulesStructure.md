# Module Graph

```mermaid
%%{
  init: {
    'theme': 'neutral'
  }
}%%

graph LR
  subgraph :client
    :client:database["database"]
    :client:core["core"]
    :client:resources["resources"]
  end
  subgraph :client:features:auth
    :client:features:auth:api["api"]
    :client:features:auth:impl["impl"]
  end
  subgraph :client:features:home
    :client:features:home:impl["impl"]
    :client:features:home:api["api"]
  end
  subgraph :client:features:user
    :client:features:user:api["api"]
    :client:features:user:impl["impl"]
  end
  subgraph :client:navigation
    :client:navigation:processor["processor"]
    :client:navigation:core["core"]
  end
  subgraph :network
    :network:api["api"]
    :network:core["core"]
    :network:serverProcessor["serverProcessor"]
  end
  :client:navigation:processor --> :client:navigation:core
  :client:database --> :client:features:user:api
  :client:database --> :client:features:auth:api
  :client:features:user:impl --> :client:features:user:api
  :client:features:user:impl --> :client:database
  :network:api --> :network:core
  :network:api --> :network:serverProcessor
  :client:core --> :client:features:user:impl
  :client:core --> :client:features:auth:impl
  :client:core --> :client:features:home:impl
  :client:core --> :client:database
  :client:core --> :client:navigation:core
  :client:core --> :network:api
  :client:core --> :network:core
  :client:core --> :client:resources
  :client:features:home:api --> :client:navigation:core
  :composeApp --> :client:core
  :client:features:auth:api --> :client:navigation:core
  :client:features:auth:impl --> :client:features:auth:api
  :client:features:auth:impl --> :client:features:home:api
  :client:features:auth:impl --> :client:database
  :client:features:auth:impl --> :client:navigation:core
  :client:features:auth:impl --> :network:api
  :client:features:auth:impl --> :network:core
  :client:features:auth:impl --> :client:resources
  :client:features:home:impl --> :client:features:home:api
  :client:features:home:impl --> :client:database
  :client:features:home:impl --> :client:navigation:core
  :client:features:home:impl --> :network:api
  :client:features:home:impl --> :network:core
  :client:features:home:impl --> :client:resources
  :client:features:home:impl --> :client:features:auth:api
  :network:serverProcessor --> :network:core
  :server --> :client:core
  :server --> :network:api
  :server --> :network:serverProcessor
```