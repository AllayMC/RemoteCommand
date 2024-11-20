# RemoteCommand

Execute server command through http interface.

This plugin is used in allay official test server to add support for auto update.

# Http API

Let's say you set http server port to 19133, then the base url is `http://localhost:19133/remotecommand`.
Please note that only POST method is allowed!

Parameters:
- `token` The token you set in `config.yml`.
- `command` The command you want to execute.

## License

This project is licensed under the LGPL-3.0 license - see the [LICENSE](LICENSE) file for details.