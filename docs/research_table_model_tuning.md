# Research Table model tuning

Model file:

`src/main/resources/assets/thaumcraft/models/block/research_table.json`

Minecraft block model coordinates use a 16-unit block grid. The research table model is 32 units wide because it spans two blocks.

## Paper stack

Edit elements named `paper_layer_0` through `paper_layer_5`.

Current A4-style portrait coordinates:

| Layer | from | to |
| --- | --- | --- |
| `paper_layer_0` | `[11.7, 16.04, 1.6]` | `[20.3, 16.05, 13.8]` |
| `paper_layer_1` | `[10.58, 16.28, 1.72]` | `[19.18, 16.29, 13.92]` |
| `paper_layer_2` | `[10.82, 16.52, 1.5]` | `[19.42, 16.53, 13.7]` |
| `paper_layer_3` | `[10.62, 16.76, 1.88]` | `[19.22, 16.77, 14.08]` |
| `paper_layer_4` | `[10.76, 17.0, 1.68]` | `[19.36, 17.01, 13.88]` |
| `paper_layer_5` | `[10.5, 17.24, 1.78]` | `[19.1, 17.25, 13.98]` |

Useful adjustments:

- Move paper left/right: change all X values.
- Move paper forward/back: change all Z values.
- Increase layer spacing: increase each layer's Y values.
- Change A4 size: keep Z length about `1.414x` the X width.

## Quill

Edit the element named `quill`.

Current coordinates:

- `from`: `[3.15, 16.72, 11.25]`
- `to`: `[3.7, 26.95, 14.55]`
- `rotation.origin`: `[3.42, 16.9, 12.55]`
- `rotation.angle`: `-22.5`

Useful adjustments:

- Put the tip deeper into the inkwell: lower the Y values and keep `origin` near the inkwell opening.
- Move the quill inside the inkwell: adjust X/Z toward the inkwell center, currently around `[3.5, 12.5]`.
- Make the quill thicker: increase the X difference between `from` and `to`.
- Vanilla block model element rotation only supports `-45`, `-22.5`, `0`, `22.5`, and `45` degrees.
