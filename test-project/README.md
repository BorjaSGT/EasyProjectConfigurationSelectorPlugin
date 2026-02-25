# Test Project for EasyProjectConfigurationSelector

This is a sample project to test the plugin.

## Example configurations

### Development
```json
{
  "environment": "development",
  "debug": true,
  "apiUrl": "http://localhost:3000",
  "timeout": 30000
}
```

### Staging
```json
{
  "environment": "staging",
  "debug": true,
  "apiUrl": "https://staging-api.example.com",
  "timeout": 10000
}
```

### Production
```json
{
  "environment": "production",
  "debug": false,
  "apiUrl": "https://api.example.com",
  "timeout": 5000
}
```

## How to use

1. Open this directory as a project in IntelliJ IDEA
2. Go to Tools > Configure Environments...
3. Select the `config.json` file
4. Add the three environments from above
5. Use the widget in the status bar or the toolbar selector to switch between environments
