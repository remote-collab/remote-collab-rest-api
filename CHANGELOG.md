# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

- API V1 needs to be deprecated until it will be removed
- E-Mail service still needs to be abstracted and implemented for use with arbitrary E-Mail service

## [0.2.0] - 2020-01-28

### Changed

- Added new Endpoint to get OpenVidu Session Token
- Fix errors in user validation and user invitation 

## [0.1.1] - 2019-11-26

### Changed

- Renamed DynamoDB store

## [0.1.0] - 2019-11-15

### Changed

- There exist two versions (v1 and v2) of the REST API now. Version 2 API endpoints now comply with common REST best practices.
  Both versions will be provided in parallel until the first major release of this service is released.
- Tests have been adjusted according to the new API versions. Since there are no breaking changes included in data models no
  additional tests have been provided.

## [0.0.1] - 2019-11-15

### Added

- This CHANGELOG file to hopefully serve as an evolving example of a
  standardized open source project CHANGELOG.
- README was completely refactored and now contains steps on how to build, run and test locally
- Code has been refactored and old viper references have been removed

[0.0.1]: https://github.com/remote-collab/remote-collab-rest-api/releases/tag/v0.0.1
[0.1.0]: https://github.com/remote-collab/remote-collab-rest-api/releases/tag/v0.1.0
[0.1.1]: https://github.com/remote-collab/remote-collab-rest-api/releases/tag/v0.1.1
