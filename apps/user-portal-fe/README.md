# User Portal Frontend

## Build iOS App

Pre-requisites:

- [EAS CLI](https://docs.expo.dev/build/introduction/): `npm install -g eas-cli`
- [Expo Account](https://expo.dev/signup)
- [Apple Developer Account](https://developer.apple.com/account/)
- Permissions for the Apple Account to create distribution certificates and provisioning profiles
- Registered iOS devices in the Apple Developer Account for this specific app  
  `eas device:list` to see the list of current devices  
  `eas device:create` to register a new device  
  `eas device:delete` to delete a device

### Local

Pre-requisites:

- XCode
- [Apple Worldwide Developer Relations Intermediate Certificate](https://www.apple.com/certificateauthority/) installed in your Keychain
- [Fastlane](https://formulae.brew.sh/formula/fastlane)

```bash
eas build -p ios --profile preview --local
```

### On EAS

```bash
eas build -p ios --profile preview
```
