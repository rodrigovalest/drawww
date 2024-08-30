import { CompressionService } from './compression.service';

fdescribe('CompressionService', () => {

  it('should compress and decompress an SVG string correctly', () => {
    const svgData = '<svg height="100" width="100"><circle cx="50" cy="50" r="40" stroke="black" stroke-width="3" fill="red" /></svg>';
    
    const compressedData = CompressionService.compressSVG(svgData);
    expect(compressedData).toBeTruthy();
    expect(compressedData).toBeInstanceOf(Uint8Array);
    expect(compressedData.length).toBeGreaterThan(0);

    const decompressedData = CompressionService.decompressSVG(compressedData);
    expect(decompressedData).toBeTruthy();
    expect(decompressedData).toBe(svgData);
  });
});
