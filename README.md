# Fixes Vanila CIT RP (1.21.5+) 
1.<b> when you accidentally put 2 or more same names and it broke entire pack </b>
<br>
<div align="center">
<img width="75%" src="https://cdn.modrinth.com/data/KexLXhaf/images/f45c301599a1b0e0428d954210a7454c757cdd1b.png">
<br>
<img width="75%" src="https://cdn.modrinth.com/data/KexLXhaf/images/27770c5640d02a22da7ff474cd39e8e4e90dfd1a.png">
<br>
<img width="75%" src="https://cdn.modrinth.com/data/KexLXhaf/images/22618847af6c62653b91b379111c840a430042d0.png">
<br>
</div>

2.<b> merge 2+ resoucepacks with renames </b>
<div align="center">
  <img width="75%" src="https://cdn.modrinth.com/data/cached_images/d80d46a118dd42995f5608cbdae164da9f4f2ff3.png">
</div>

### How it works:
for example, we have 3 resource packs with paper item:
```json
{
  "model": {
    "type": "select",
    "property": "component",
    "component": "custom_name",

    "cases": [
      {
        "when": "500",
        "model": {
          "type": "model",
          "model": "minecraft:item/paper_null"
        }
      }
    ],
    "fallback": {
      "type": "model",
      "model": "minecraft:item/paper"
    }
  }
}
```

```json
{
  "model": {
    "type": "select",
    "property": "component",
    "component": "custom_name",

    "cases": [
      {
        "when": "300",
        "model": {
          "type": "model",
          "model": "minecraft:item/paper_null_2"
        }
      }
    ],
    "fallback": {
      "type": "model",
      "model": "minecraft:item/paper"
    }
  }
}
```

```json
{
  "model": {
    "type": "select",
    "property": "custom_model_data",

    "cases": [
      {
        "when": "500",
        "model": {
          "type": "model",
          "model": "minecraft:item/paper_null_2"
        }
      }
    ],
    "fallback": {
      "type": "model",
      "model": "minecraft:item/paper"
    }
  }
}
```

and the mod merge all cases into one and result will be something like:

```json

{
  "model": {
    "type": "minecraft:select",
    "property": "component",
    "component": "custom_name",
    "cases": [
      {
        "when": "500",
        "model": {
          "type": "model",
          "model": "minecraft:item/paper_null"
        }
      },
      {
        "when": "300",
        "model": {
          "type": "model",
          "model": "minecraft:item/paper_null_2"
        }
      }
    ],
    "fallback": {
      "type": "minecraft:select",
      "property": "custom_model_data",
      "cases": [
        {
          "when": "500",
          "model": {
            "type": "model",
            "model": "minecraft:item/paper_null_2"
          }
        }
      ],
      "fallback": {
        "type": "model",
        "model": "minecraft:item/paper"
      }
    }
  }
}


```

Tested on 1.21.6 and 1.21.8, also can break some other stuff on ur clinet.
**please, report me in [discord](https://discord.com/invite/sBpHZUBebQ) if something goes wrong, thanks**
