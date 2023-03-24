# Eimer Archive Backend

[![Discord Server](https://img.shields.io/discord/979589333524820018?color=7289da&label=DISCORD&style=flat-square&logo=appveyor)](https://discord.gg/k8RcgxpnBS)

Eimer Archive is a website that hosts files as an archive, it focuses mainly on Minecraft server software but accepts other files. The backend is made with Springboot.

## Usage

### Requirements

- Java 17
- MySQL/MariaDB database

### Setup

Once you create a database for this to connect to you need to run these commands to get it setup

```mysql
INSERT INTO roles(name) VALUES('ROLE_USER');
INSERT INTO roles(name) VALUES('ROLE_ARCHIVER');
INSERT INTO roles(name) VALUES('ROLE_MODERATOR');
INSERT INTO roles(name) VALUES('ROLE_ADMIN');
```