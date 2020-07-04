# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

### Changed

* Update project dependencies:
    * Package:
        * Update moshi dependency to `1.9.3`
        * Update okio dependency to `2.6.0`
        * Update ktor dependency to `1.3.2`
    * Test:
        * Update junit dependency to `5.6.2`
        * Update truth dependency to `1.0.1`
    * Sample:
        * Update logback dependency to `1.2.3`

## [2.0.0] - 2020-07-03

First release of the fork. This release doesn't contain any implementation or API changes. The only changes are to the
artifact and package import names.

### Changed

* Artifact group is now `com.hypercubetools`.
* Artifact name is now `ktor-mosho-server`.
* Package namespace is now `com.hypercubetools.ktor.moshi`.
* The CI pipeline now publishes artifacts to JCenter.

## [1.0.1] - 2018-11-25

### Changed

* Update for Ktor 1.0

## [1.0.0] - 2018-09-12

* Initial Release
