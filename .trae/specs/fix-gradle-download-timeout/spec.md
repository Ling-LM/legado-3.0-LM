# 修复 Gradle 分发包下载超时 Spec

## Why
在中国大陆网络环境下，从 `services.gradle.org` 下载 Gradle 分发包经常因连接超时（`java.net.SocketTimeoutException`）而失败，导致项目无法构建。同时，Maven 仓库的依赖下载也可能受影响。

## What Changes
- 将 `gradle-wrapper.properties` 中的 `distributionUrl` 从官方地址切换为腾讯云 Gradle 镜像地址
- 在 `settings.gradle` 中启用已注释的国内镜像仓库（阿里云、华为云），替换或补充官方仓库

## Impact
- Affected specs: 构建环境配置
- Affected code:
  - `gradle/wrapper/gradle-wrapper.properties`
  - `settings.gradle`

## ADDED Requirements

### Requirement: Gradle 分发包使用国内镜像下载
系统 SHALL 将 Gradle Wrapper 的分发包下载地址配置为国内可访问的镜像地址，以避免连接超时。

#### Scenario: 构建时下载 Gradle 分发包
- **WHEN** 开发者执行 Gradle 构建命令
- **THEN** Gradle Wrapper 从腾讯云镜像（`https://mirrors.cloud.tencent.com/gradle/`）下载对应版本的分发包，而非从 `services.gradle.org` 下载

### Requirement: Maven 依赖使用国内镜像仓库
系统 SHALL 在 `settings.gradle` 中启用国内镜像仓库，确保 Maven 依赖可以正常下载。

#### Scenario: 构建时解析依赖
- **WHEN** Gradle 解析项目依赖
- **THEN** 优先从国内镜像仓库（阿里云、华为云）下载依赖，避免因官方仓库访问超时导致构建失败
