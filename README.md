# Easy Project Configuration Selector

A plugin for IntelliJ IDEA and Rider that allows you to quickly switch between different environment configurations.

## Features

- **Environment selector in main toolbar**: Dropdown in the top toolbar to switch between environments with a single click
- **Also available in status bar**: Alternative widget at the bottom of the window
- **Visual environment management**: Intuitive interface to add, edit and delete environments
- **Automatic persistence**: Configuration is saved in `.idea/easyEnvironmentConfigurationsPlugin.xml`
- **Instant switching**: Configuration file content is updated automatically

## Usage

### 1. Configure the plugin

1. Go to **Tools > Configure Environments**
2. Select the configuration file you want to manage (e.g., `appsettings.json`, `.env`, etc.)
3. Add environments with the `+` button:
   - **Name**: dev, staging, production, etc.
   - **Content**: The complete file content for that environment

### 2. Switch environments

**Option A - From the Toolbar (Recommended)**:
- Look for the "Env: [name]" selector in the top toolbar
- Click on it
- Select the desired environment
- You can also click the settings icon (⚙️) next to it to open the configuration dialog

**Option B - From the Status Bar**:
- Click on the widget in the status bar (bottom corner)
- Select the desired environment

The file content will be updated automatically.

### 3. Project configuration

The configuration is stored in `.idea/easyEnvironmentConfigurationsPlugin.xml` and contains:
- Path to the configuration file
- List of environments and their contents
- Currently selected environment

## Development

### Build

```bash
./gradlew build
```

### Run in development IDE

```bash
./gradlew runIde
```

Or use the "Run IDE with Plugin" run configuration in IntelliJ IDEA.

**IMPORTANT**: Once the test IDE opens, you must open a project for the "Configure Environments..." action to be available. You can use the included test project in `test-project/`.

### Generate distributable plugin

```bash
./gradlew buildPlugin
```

The `.zip` file will be generated in `build/distributions/`

### Test the plugin

1. Run `./gradlew runIde`
2. In the IDE that opens, go to `File > Open`
3. Navigate to `EasyProjectConfigurationSelector/test-project` and open it
4. Now go to `Tools > Configure Environments...`
5. Follow the instructions in `test-project/README.md`

See `TESTING.md` for detailed testing instructions.

## Requirements

- IntelliJ IDEA 2025.1.4.1 or higher
- JDK 21

## Project structure

```
src/main/kotlin/com/bsg/easyconfig/
├── models/              # Data models
│   ├── EnvironmentConfig.kt
│   └── ProjectSettings.kt
├── services/            # Project services
│   └── EnvironmentConfigurationService.kt
├── widgets/             # UI widgets
│   ├── EnvironmentSelectorWidget.kt
│   └── EnvironmentSelectorWidgetFactory.kt
├── ui/                  # Dialogs and UI
│   ├── EnvironmentConfigDialog.kt
│   └── EnvironmentEditDialog.kt
└── actions/             # Plugin actions
    ├── ConfigureEnvironmentsAction.kt
    ├── ConfigureEnvironmentsToolbarAction.kt
    └── EnvironmentComboBoxAction.kt
```

## License

Copyright (c) 2026 Borja Salamanca
