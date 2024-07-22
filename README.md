# Avaya Experience Platform™ Android Omni SDK

### :warning: Disclaimer

    Installing, downloading, copying or using this SDK is subject to terms and
    conditions available in the LICENSE file.

## Key Resources
- [AXP Core](./core.md)
- [AXP Messaging](./messaging.md)
- [Messaging-UI](./messaging-ui.md)
- [AXP Calling](./calling.md)

## Prerequisites

To use this SDK, you need an account registered with the Avaya Experience
Platform™, and have that account provisioned to enable use of the client APIs.

Once you have an account, it must be provisioned for the following two items:

1. **Integration ID**

   To create an integration, follow the instructions in [Creating an Omni SDK
   Integration][omni-integration]. The two services you can enable there
   (**Messaging** and **WebRTC Voice**) each correspond to an SDK module,
   and you must enable the services for the modules that you will use.

2. **Application Key**

   To enable remote access to the AXP APIs, you need to get an application key
   as described in [How to Authenticate with Avaya Experience Platform™
   APIs][axp-auth].

## License

View [LICENSE](./LICENSE)


## Changelog

View [CHANGELOG.md](./CHANGELOG.md)

[omni-integration]: https://documentation.avaya.com/bundle/ExperiencePlatform_Administering_10/page/Creating_an_Omni_SDK_integration.html
[axp-auth]: https://developers.avayacloud.com/avaya-experience-platform/docs/how-to-authenticate-with-axp-apis
