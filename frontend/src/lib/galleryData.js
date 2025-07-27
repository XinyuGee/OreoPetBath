const galleryImages = Array.from({ length: 30 }, (_, i) => ({
  src: `/galleryPhotos/photo${i + 1}.jpg`,
  alt: `Gallery photo ${i + 1}`,
}));

export default galleryImages;
