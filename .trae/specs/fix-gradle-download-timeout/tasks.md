# Tasks

- [x] Task 1: 修改 gradle-wrapper.properties 使用腾讯云 Gradle 镜像地址
  - [x] 将 `distributionUrl` 从 `https\://services.gradle.org/distributions/gradle-8.13-bin.zip` 改为 `https\://mirrors.cloud.tencent.com/gradle/gradle-8.13-bin.zip`

- [x] Task 2: 在 settings.gradle 中启用国内镜像仓库
  - [x] 在 `pluginManagement.repositories` 中启用阿里云和华为云镜像仓库（取消注释）
  - [x] 在 `dependencyResolutionManagement.repositories` 中启用阿里云和华为云镜像仓库（取消注释）

# Task Dependencies
- Task 1 和 Task 2 无依赖关系，可并行执行
