# Hiero Enterprise Java - Architecture Diagram

## High-Level Architecture

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                           Your Application                                 │
│  ┌─────────────────┐  ┌─────────────────┐  ┌─────────────────────────────┐ │
│  │   REST API      │  │   Business      │  │   Background                │ │
│  │   Controllers   │  │   Services      │  │   Services                  │ │
│  └─────────────────┘  └─────────────────┘  └─────────────────────────────┘ │
├─────────────────────────────────────────────────────────────────────────────┤
│                        Spring Boot / MicroProfile                         │
│  ┌─────────────────┐  ┌─────────────────┐  ┌─────────────────────────────┐ │
│  │   @EnableHiero  │  │ Auto-Config     │  │   Properties                │ │
│  │   Annotation    │  │   Classes       │  │   Support                   │ │
│  └─────────────────┘  └─────────────────┘  └─────────────────────────────┘ │
├─────────────────────────────────────────────────────────────────────────────┤
│                    Hiero Enterprise Library                               │
│  ┌─────────────────┐  ┌─────────────────┐  ┌─────────────────────────────┐ │
│  │   Client Layer  │  │ Protocol Layer  │  │   Mirror Node Layer         │ │
│  │                 │  │                 │  │                             │ │
│  │ • AccountClient │  │ • ProtocolLayer │  │ • AccountRepository         │ │
│  │ • FileClient    │  │   Client        │  │ • NetworkRepository         │ │
│  │ • TokenClient   │  │ • Transaction   │  │ • TransactionRepository     │ │
│  │ • NftClient     │  │   Management    │  │ • TokenRepository           │ │
│  │ • TopicClient   │  │ • Error         │  │ • NftRepository             │ │
│  │ • ContractClient│  │   Handling      │  │ • TopicRepository           │ │
│  └─────────────────┘  └─────────────────┘  └─────────────────────────────┘ │
├─────────────────────────────────────────────────────────────────────────────┤
│                            Hedera SDK                                     │
│  ┌─────────────────┐  ┌─────────────────┐  ┌─────────────────────────────┐ │
│  │   Client        │  │   Transactions  │  │   Queries                   │ │
│  │   Management    │  │   & Records     │  │   & Responses               │ │
│  └─────────────────┘  └─────────────────┘  └─────────────────────────────┘ │
├─────────────────────────────────────────────────────────────────────────────┤
│                           Hiero Network                                   │
│  ┌─────────────────┐  ┌─────────────────┐  ┌─────────────────────────────┐ │
│  │   Consensus     │  │   State         │  │   Mirror                    │ │
│  │   Nodes         │  │   Management    │  │   Nodes                     │ │
│  └─────────────────┘  └─────────────────┘  └─────────────────────────────┘ │
└─────────────────────────────────────────────────────────────────────────────┘
```

## Detailed Component Flow

### 1. Application Startup Flow
```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   @EnableHiero  │───▶│ Auto-Config     │───▶│ Bean Creation   │
│   Annotation    │    │   Detection     │    │   & Injection   │
└─────────────────┘    └─────────────────┘    └─────────────────┘
                                │
                                ▼
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   Properties    │◀───│ Configuration   │◀───│   HieroContext  │
│   Loading       │    │   Processing    │    │   Creation      │
└─────────────────┘    └─────────────────┘    └─────────────────┘
```

### 2. Transaction Flow
```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   Your Code     │───▶│   Client        │───▶│   Protocol      │
│   (Business)    │    │   Interface     │    │   Layer         │
└─────────────────┘    └─────────────────┘    └─────────────────┘
                                │
                                ▼
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   Request       │───▶│   Hedera SDK    │───▶│   Network       │
│   Object        │    │   Transaction   │    │   Processing    │
└─────────────────┘    └─────────────────┘    └─────────────────┘
                                │
                                ▼
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   Response      │◀───│   Network       │◀───│   Transaction   │
│   Object        │    │   Response      │    │   Confirmation  │
└─────────────────┘    └─────────────────┘    └─────────────────┘
```

### 3. Query Flow
```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   Your Code     │───▶│   Client        │───▶│   Protocol      │
│   (Query)       │    │   Interface     │    │   Layer         │
└─────────────────┘    └─────────────────┘    └─────────────────┘
                                │
                                ▼
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   Query         │───▶│   Hedera SDK    │───▶│   Mirror Node   │
│   Request       │    │   Query         │    │   or Network    │
└─────────────────┘    └─────────────────┘    └─────────────────┘
                                │
                                ▼
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   Data          │◀───│   Query         │◀───│   Historical    │
│   Object        │    │   Response      │    │   Data          │
└─────────────────┘    └─────────────────┘    └─────────────────┘
```

## Module Dependencies

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                              Main Project                                  │
├─────────────────────────────────────────────────────────────────────────────┤
│  ┌─────────────────┐  ┌─────────────────┐  ┌─────────────────────────────┐ │
│  │   Spring        │  │   MicroProfile  │  │   Base Module               │ │
│  │   Module        │  │   Module        │  │   (Core)                    │ │
│  │                 │  │                 │  │                             │ │
│  │ • @EnableHiero  │  │ • CDI Support   │  │ • Client Interfaces         │ │
│  │ • Auto-Config   │  │ • Config        │  │ • Protocol Layer            │ │
│  │ • Properties    │  │ • Properties    │  │ • Data Models               │ │
│  │ • Bean Creation │  │ • Bean Creation │  │ • Mirror Node               │ │
│  └─────────────────┘  └─────────────────┘  └─────────────────────────────┘ │
├─────────────────────────────────────────────────────────────────────────────┤
│                              Dependencies                                  │
├─────────────────────────────────────────────────────────────────────────────┤
│  ┌─────────────────┐  ┌─────────────────┐  ┌─────────────────────────────┐ │
│  │   Spring Boot   │  │   MicroProfile  │  │   Hedera SDK               │ │
│  │   Framework     │  │   Framework     │  │                             │ │
│  │                 │  │                 │  │ • Client Management         │ │
│  │ • Auto-Config   │  │ • CDI           │  │ • Transaction Building      │ │
│  │ • Properties    │  │ • Config        │  │ • Network Communication     │ │
│  │ • Bean Creation │  │ • Bean Creation │  │ • Response Handling         │ │
│  └─────────────────┘  └─────────────────┘  └─────────────────────────────┘ │
└─────────────────────────────────────────────────────────────────────────────┘
```

## Data Model Relationships

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   Account       │    │   Token         │    │   File          │
│                 │    │                 │    │                 │
│ • accountId     │    │ • tokenId       │    │ • fileId        │
│ • publicKey     │    │ • name          │    │ • contents      │
│ • privateKey    │    │ • symbol        │    │ • metadata      │
└─────────────────┘    └─────────────────┘    └─────────────────┘
         │                       │                       │
         ▼                       ▼                       ▼
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   AccountInfo   │    │   TokenInfo     │    │   FileInfo      │
│                 │    │                 │    │                 │
│ • balance       │    │ • supply        │    │ • size          │
│ • transactions  │    │ • decimals      │    │ • expiration    │
│ • metadata      │    │ • fees          │    │ • keys          │
└─────────────────┘    └─────────────────┘    └─────────────────┘
```

## Configuration Flow

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   application   │───▶│   HieroProperties│───▶│   HieroConfig   │
│   .properties   │    │                 │    │                 │
└─────────────────┘    └─────────────────┘    └─────────────────┘
                                │
                                ▼
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   Environment   │───▶│   HieroContext  │───▶│   Client        │
│   Variables     │    │                 │    │   Creation      │
└─────────────────┘    └─────────────────┘    └─────────────────┘
```

## Error Handling Flow

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   Network       │───▶│   Hedera SDK    │───▶│   Protocol      │
│   Error         │    │   Exception     │    │   Layer         │
└─────────────────┘    └─────────────────┘    └─────────────────┘
                                │
                                ▼
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   HieroException│◀───│   Exception     │◀───│   Exception     │
│   (Wrapped)     │    │   Wrapping      │    │   Translation   │
└─────────────────┘    └─────────────────┘    └─────────────────┘
                                │
                                ▼
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   Your Code     │◀───│   Exception     │◀───│   Error         │
│   Error         │    │   Handling      │    │   Processing    │
│   Handling      │    │   Logic         │    │   Logic         │
└─────────────────┘    └─────────────────┘    └─────────────────┘
```

## Key Integration Points

### 1. Spring Boot Integration
- **@EnableHiero** annotation triggers auto-configuration
- **HieroAutoConfiguration** creates all necessary beans
- **HieroProperties** binds configuration properties
- **Dependency injection** provides clients to your services

### 2. MicroProfile Integration
- **CDI beans** for dependency injection
- **Configuration** properties support
- **Health checks** and metrics
- **REST client** integration

### 3. Blockchain Integration
- **Hedera SDK** for low-level communication
- **Protocol layer** for transaction management
- **Mirror node** for historical data queries
- **Error handling** and retry logic

### 4. Data Flow Integration
- **Request/Response** objects for protocol communication
- **Data models** for business objects
- **Validation** and error handling
- **Serialization** and deserialization

This architecture provides a clean separation of concerns while maintaining flexibility and extensibility for different use cases.