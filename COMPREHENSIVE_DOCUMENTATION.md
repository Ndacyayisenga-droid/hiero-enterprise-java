# Hiero Enterprise Java - Comprehensive Documentation

## Table of Contents
1. [Introduction](#introduction)
2. [Project Overview](#project-overview)
3. [Architecture Overview](#architecture-overview)
4. [Module Structure](#module-structure)
5. [Core Concepts](#core-concepts)
6. [How Everything Works Together](#how-everything-works-together)
7. [Detailed Module Breakdown](#detailed-module-breakdown)
8. [Configuration and Setup](#configuration-and-setup)
9. [Usage Examples](#usage-examples)
10. [Key Classes and Their Roles](#key-classes-and-their-roles)
11. [Data Flow](#data-flow)
12. [Testing and Development](#testing-and-development)
13. [Common Patterns](#common-patterns)
14. [Troubleshooting](#troubleshooting)

## Introduction

Welcome to the **Hiero Enterprise Java** codebase! This is a Java library that provides enterprise-grade integration with the Hiero blockchain network. If you're new to Java and blockchain development, don't worry - this documentation will guide you through everything step by step.

### What is Hiero?
Hiero is a blockchain network (currently based on Hedera Hashgraph) that allows you to:
- Create and manage accounts
- Store files on the blockchain
- Create and transfer tokens (fungible and NFTs)
- Deploy and interact with smart contracts
- Create topics for messaging
- Query network information

### What does this library do?
This library provides a **high-level, easy-to-use interface** for Java applications to interact with the Hiero blockchain. It handles all the complex blockchain communication details so you can focus on your business logic.

## Project Overview

### Project Structure
```
hiero-enterprise/
├── hiero-enterprise-base/          # Core functionality
├── hiero-enterprise-spring/        # Spring Boot integration
├── hiero-enterprise-microprofile/  # MicroProfile integration
├── hiero-enterprise-test/          # Testing utilities
├── hiero-enterprise-spring-sample/ # Spring Boot example
├── hiero-enterprise-microprofile-sample/ # MicroProfile example
└── pom.xml                         # Main Maven configuration
```

### Key Technologies Used
- **Java 21** - Modern Java features
- **Maven** - Build and dependency management
- **Spring Boot** - Enterprise Java framework
- **Hedera SDK** - Blockchain communication
- **MicroProfile** - Enterprise Java standards
- **JUnit 5** - Testing framework

## Architecture Overview

### High-Level Architecture
```
┌─────────────────────────────────────────────────────────────┐
│                    Your Application                        │
├─────────────────────────────────────────────────────────────┤
│              Spring Boot / MicroProfile                    │
├─────────────────────────────────────────────────────────────┤
│              Hiero Enterprise Library                      │
│  ┌─────────────────┐  ┌─────────────────┐  ┌─────────────┐ │
│  │   Client Layer  │  │  Protocol Layer │  │ Mirror Node │ │
│  │                 │  │                 │  │   Layer     │ │
│  └─────────────────┘  └─────────────────┘  └─────────────┘ │
├─────────────────────────────────────────────────────────────┤
│                    Hedera SDK                             │
├─────────────────────────────────────────────────────────────┤
│                    Hiero Network                          │
└─────────────────────────────────────────────────────────────┘
```

### Three-Layer Architecture

1. **Client Layer** - High-level, business-focused APIs
2. **Protocol Layer** - Low-level blockchain communication
3. **Mirror Node Layer** - Query historical data

## Module Structure

### 1. hiero-enterprise-base
**Purpose**: Core functionality that works with any Java framework

**Key Components**:
- `AccountClient` - Create and manage accounts
- `FileClient` - Store and retrieve files
- `TokenClient` - Handle fungible tokens
- `NftClient` - Handle non-fungible tokens
- `SmartContractClient` - Deploy and interact with smart contracts
- `TopicClient` - Create and manage messaging topics

### 2. hiero-enterprise-spring
**Purpose**: Spring Boot integration

**Key Components**:
- `@EnableHiero` annotation
- Auto-configuration classes
- Spring Boot properties support

### 3. hiero-enterprise-microprofile
**Purpose**: MicroProfile integration (for Quarkus, Helidon, etc.)

### 4. Sample Applications
**Purpose**: Working examples showing how to use the library

## Core Concepts

### 1. Operator Account
The **operator account** is the account that pays for all transactions. Think of it as your "service account" that funds all operations.

```java
// Configuration
spring.hiero.accountId=0.0.12345678
spring.hiero.privateKey=your-private-key
```

### 2. Clients
Each client provides a specific set of blockchain operations:

```java
@Autowired
private AccountClient accountClient;    // Account operations
@Autowired
private FileClient fileClient;          // File operations
@Autowired
private TokenClient tokenClient;        // Token operations
```

### 3. Transactions vs Queries
- **Transactions**: Modify the blockchain state (create account, transfer tokens)
- **Queries**: Read data from the blockchain (get balance, read file)

## How Everything Works Together

### 1. Application Startup
```java
@SpringBootApplication
@EnableHiero  // ← This is the magic!
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
```

**What happens when you start the application:**

1. **Spring Boot starts** and looks for `@EnableHiero`
2. **Auto-configuration kicks in** (`HieroAutoConfiguration`)
3. **Configuration is loaded** from `application.properties`
4. **HieroContext is created** with your account details
5. **All clients are created** and injected into your beans
6. **Your application is ready** to use blockchain features!

### 2. Making a Blockchain Call
```java
@Service
public class MyService {
    @Autowired
    private AccountClient accountClient;
    
    public void createAccount() {
        Account newAccount = accountClient.createAccount();
        System.out.println("Created account: " + newAccount.accountId());
    }
}
```

**What happens when you call `createAccount()`:**

1. **AccountClient** receives the request
2. **ProtocolLayerClient** creates the transaction
3. **Hedera SDK** sends transaction to network
4. **Network** processes and confirms transaction
5. **Response** returns with new account details
6. **Account object** is created and returned

### 3. Data Flow Example
```
Your Code → AccountClient → ProtocolLayerClient → Hedera SDK → Hiero Network
     ↑                                                              ↓
     └────────────── Account Object ←──────────────────────────────┘
```

## Detailed Module Breakdown

### hiero-enterprise-base

#### Core Interfaces
```java
// Main context that holds connection info
public interface HieroContext {
    Account getOperatorAccount();
    Client getClient();
}

// High-level client interfaces
public interface AccountClient {
    Account createAccount(Hbar initialBalance);
    void deleteAccount(Account account);
    Hbar getAccountBalance(AccountId accountId);
}
```

#### Implementation Classes
```java
// Actual implementations that do the work
public class AccountClientImpl implements AccountClient {
    private final ProtocolLayerClient client;
    
    public Account createAccount(Hbar initialBalance) {
        // 1. Create request object
        AccountCreateRequest request = AccountCreateRequest.of(initialBalance);
        
        // 2. Send to protocol layer
        AccountCreateResult result = client.executeAccountCreateTransaction(request);
        
        // 3. Return new account
        return result.newAccount();
    }
}
```

#### Data Models
```java
// Immutable data objects
public record Account(
    @NonNull AccountId accountId,
    @NonNull PublicKey publicKey,
    @NonNull PrivateKey privateKey
) {
    // Validation and utility methods
}
```

### hiero-enterprise-spring

#### Auto-Configuration
```java
@AutoConfiguration
@EnableConfigurationProperties({HieroProperties.class})
public class HieroAutoConfiguration {
    
    @Bean
    HieroContext hieroContext(HieroConfig hieroConfig) {
        return hieroConfig.createHieroContext();
    }
    
    @Bean
    AccountClient accountClient(ProtocolLayerClient protocolLayerClient) {
        return new AccountClientImpl(protocolLayerClient);
    }
    
    // ... more beans for other clients
}
```

#### Properties Support
```java
@ConfigurationProperties(prefix = "spring.hiero")
public class HieroProperties {
    private String accountId;
    private String privateKey;
    private String network;
    // ... getters and setters
}
```

## Configuration and Setup

### 1. Maven Dependencies
```xml
<dependency>
    <groupId>com.open-elements.hiero</groupId>
    <artifactId>hiero-enterprise-spring</artifactId>
    <version>0.20.0-SNAPSHOT</version>
</dependency>
```

### 2. Application Properties
```properties
# Required: Your account details
spring.hiero.accountId=0.0.12345678
spring.hiero.privateKey=302e020100300506032b657004220420...

# Optional: Network configuration
spring.hiero.network=hedera-testnet
spring.hiero.mirrorNodeSupported=true
```

### 3. Environment Variables
```bash
export HEDERA_ACCOUNT_ID=0.0.12345678
export HEDERA_PRIVATE_KEY=302e020100300506032b657004220420...
export HEDERA_NETWORK=testnet
```

## Usage Examples

### 1. Basic Account Operations
```java
@Service
public class AccountService {
    @Autowired
    private AccountClient accountClient;
    
    public void accountOperations() {
        // Create a new account
        Account newAccount = accountClient.createAccount();
        
        // Get account balance
        Hbar balance = accountClient.getAccountBalance(newAccount.accountId());
        
        // Delete account (transfer funds to operator)
        accountClient.deleteAccount(newAccount);
    }
}
```

### 2. File Operations
```java
@Service
public class FileService {
    @Autowired
    private FileClient fileClient;
    
    public void fileOperations() {
        // Create a file
        FileId fileId = fileClient.createFile("Hello Blockchain!".getBytes());
        
        // Read file contents
        byte[] content = fileClient.readFile(fileId);
        String text = new String(content);
        
        // Update file
        fileClient.updateFile(fileId, "Updated content".getBytes());
    }
}
```

### 3. Token Operations
```java
@Service
public class TokenService {
    @Autowired
    private FungibleTokenClient tokenClient;
    
    public void tokenOperations() {
        // Create a token
        TokenInfo token = tokenClient.createToken("MyToken", "MTK", 1000);
        
        // Mint more tokens
        tokenClient.mintToken(token.tokenId(), 500);
        
        // Transfer tokens
        tokenClient.transferToken(token.tokenId(), 
            AccountId.fromString("0.0.123"), 
            AccountId.fromString("0.0.456"), 
            100);
    }
}
```

## Key Classes and Their Roles

### Core Classes

#### 1. HieroContext
**Role**: Central configuration and connection management
```java
public interface HieroContext {
    Account getOperatorAccount();  // Your service account
    Client getClient();           // Low-level Hedera client
}
```

#### 2. ProtocolLayerClient
**Role**: Low-level blockchain communication
```java
public interface ProtocolLayerClient {
    AccountCreateResult executeAccountCreateTransaction(AccountCreateRequest request);
    AccountBalanceResponse executeAccountBalanceQuery(AccountBalanceRequest request);
    // ... many more methods
}
```

#### 3. Client Implementations
**Role**: High-level business operations
```java
public class AccountClientImpl implements AccountClient {
    private final ProtocolLayerClient client;
    
    public Account createAccount(Hbar initialBalance) {
        // Convert business call to protocol call
        AccountCreateRequest request = AccountCreateRequest.of(initialBalance);
        AccountCreateResult result = client.executeAccountCreateTransaction(request);
        return result.newAccount();
    }
}
```

### Data Classes

#### 1. Account
**Role**: Represents a blockchain account
```java
public record Account(
    @NonNull AccountId accountId,
    @NonNull PublicKey publicKey,
    @NonNull PrivateKey privateKey
) {
    // Immutable data object
}
```

#### 2. Request/Response Objects
**Role**: Protocol-level data transfer
```java
public record AccountCreateRequest(Hbar initialBalance) {
    public static AccountCreateRequest of(Hbar initialBalance) {
        return new AccountCreateRequest(initialBalance);
    }
}
```

## Data Flow

### 1. Transaction Flow
```
Your Code
    ↓
AccountClient.createAccount()
    ↓
AccountClientImpl.createAccount()
    ↓
AccountCreateRequest.of()
    ↓
ProtocolLayerClient.executeAccountCreateTransaction()
    ↓
Hedera SDK Transaction
    ↓
Hiero Network
    ↓
AccountCreateResult
    ↓
Account object
    ↓
Your Code
```

### 2. Query Flow
```
Your Code
    ↓
AccountClient.getAccountBalance()
    ↓
AccountClientImpl.getAccountBalance()
    ↓
AccountBalanceRequest.of()
    ↓
ProtocolLayerClient.executeAccountBalanceQuery()
    ↓
Hedera SDK Query
    ↓
Hiero Network
    ↓
AccountBalanceResponse
    ↓
Hbar balance
    ↓
Your Code
```

### 3. Error Handling Flow
```
Exception occurs in Hedera SDK
    ↓
ProtocolLayerClient catches exception
    ↓
Wraps in HieroException
    ↓
Client implementation catches HieroException
    ↓
Re-throws or handles appropriately
    ↓
Your code receives HieroException
```

## Testing and Development

### 1. Running Tests
```bash
# Set up test environment
export HEDERA_ACCOUNT_ID=0.0.12345678
export HEDERA_PRIVATE_KEY=your-test-key
export HEDERA_NETWORK=testnet

# Run tests
./mvnw test
```

### 2. Building the Project
```bash
# Clean build
./mvnw clean install

# Run with tests
./mvnw verify

# Skip tests
./mvnw install -DskipTests
```

### 3. Sample Applications
```bash
# Run Spring Boot sample
cd hiero-enterprise-spring-sample
./mvnw spring-boot:run

# Run MicroProfile sample
cd hiero-enterprise-microprofile-sample
./mvnw quarkus:dev
```

## Common Patterns

### 1. Dependency Injection Pattern
```java
@Service
public class MyService {
    private final AccountClient accountClient;
    private final FileClient fileClient;
    
    public MyService(AccountClient accountClient, FileClient fileClient) {
        this.accountClient = accountClient;
        this.fileClient = fileClient;
    }
}
```

### 2. Exception Handling Pattern
```java
public void safeOperation() {
    try {
        Account account = accountClient.createAccount();
        // Success handling
    } catch (HieroException e) {
        // Handle blockchain-specific errors
        log.error("Blockchain operation failed", e);
    } catch (Exception e) {
        // Handle other errors
        log.error("Unexpected error", e);
    }
}
```

### 3. Configuration Pattern
```java
@Configuration
public class HieroConfig {
    
    @Bean
    @ConditionalOnProperty(name = "spring.hiero.enabled", havingValue = "true")
    public HieroContext hieroContext(HieroProperties properties) {
        // Only create if enabled
        return new HieroContextImpl(properties);
    }
}
```

## Troubleshooting

### Common Issues

#### 1. "Account not found" errors
**Cause**: Invalid account ID or network configuration
**Solution**: 
- Verify account ID format (should be like "0.0.12345678")
- Check network configuration (testnet/mainnet)
- Ensure account exists on the specified network

#### 2. "Insufficient balance" errors
**Cause**: Operator account doesn't have enough HBAR
**Solution**:
- Add HBAR to your operator account
- Use testnet faucet for development
- Check account balance before operations

#### 3. "Invalid private key" errors
**Cause**: Wrong key format or corrupted key
**Solution**:
- Use DER-encoded ECDSA private key
- Verify key format from Hedera portal
- Check for extra spaces or encoding issues

#### 4. "Network connection" errors
**Cause**: Network issues or wrong endpoints
**Solution**:
- Check internet connection
- Verify network configuration
- Try different network endpoints

### Debug Tips

#### 1. Enable Debug Logging
```properties
# application.properties
logging.level.com.openelements.hiero=DEBUG
logging.level.com.hedera.hashgraph=DEBUG
```

#### 2. Check Configuration
```java
@Autowired
private HieroContext hieroContext;

public void debugConfig() {
    System.out.println("Operator: " + hieroContext.getOperatorAccount());
    System.out.println("Network: " + hieroContext.getClient().getNetworkName());
}
```

#### 3. Test Network Connectivity
```java
public void testConnection() {
    try {
        Hbar balance = accountClient.getOperatorAccountBalance();
        System.out.println("Connection OK, balance: " + balance);
    } catch (Exception e) {
        System.err.println("Connection failed: " + e.getMessage());
    }
}
```

## Next Steps

### 1. Explore the Sample Applications
- Run the Spring Boot sample
- Examine the code structure
- Try modifying the examples

### 2. Read the Source Code
- Start with `AccountClient` and its implementation
- Follow the data flow through the layers
- Understand the protocol layer

### 3. Build Your Own Application
- Create a new Spring Boot project
- Add the Hiero dependency
- Implement your blockchain use case

### 4. Learn More About
- **Hedera Hashgraph**: The underlying blockchain
- **Spring Boot**: The Java framework
- **Blockchain Concepts**: Accounts, tokens, smart contracts
- **Java Records**: Modern Java data classes

## Conclusion

This codebase provides a **clean, enterprise-ready interface** for Java applications to interact with the Hiero blockchain. The layered architecture makes it easy to understand and extend, while the Spring Boot integration makes it simple to use in modern Java applications.

The key to understanding this codebase is to follow the **data flow**: from your business logic, through the client layer, down to the protocol layer, and finally to the blockchain network. Each layer has a specific responsibility and the interfaces are designed to be intuitive and easy to use.

Happy coding with Hiero! 🚀