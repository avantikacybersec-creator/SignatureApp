export const getPdfCoordinates = (
  clientX,
  clientY,
  pdfRect
) => {

  const x = clientX - pdfRect.left;
  const y = pdfRect.bottom - clientY;

  return { x, y };
};