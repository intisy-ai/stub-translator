import { build } from "esbuild";

await build({
  bundle: true,
  platform: "node",
  format: "esm",
  target: "node20",
  entryPoints: ["src/index.ts"],
  outdir: "dist",
  logLevel: "info",
});
