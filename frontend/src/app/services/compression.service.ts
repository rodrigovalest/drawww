import * as pako from 'pako';

export class CompressionService {

  static compressSVG(svgData: string): Uint8Array {
    return pako.deflate(svgData);
  }

  static decompressSVG(compressedData: Uint8Array): string {
    const decompressedData = pako.inflate(compressedData);
    const text = new TextDecoder().decode(decompressedData);
    return text;
  }
}
