import { useState } from "react";

function SignatureCanvas({ onPositionSelect }) {

  const [position, setPosition] =
    useState(null);

  const handleClick = (e) => {

    const rect =
      e.currentTarget.getBoundingClientRect();

    const x = e.clientX - rect.left;
    const y = rect.bottom - e.clientY;

    setPosition({ x, y });

    onPositionSelect({
      x,
      y
    });
  };

  return (
    <div
      onClick={handleClick}
      style={{
        border: "2px solid black",
        height: "600px",
        cursor: "crosshair"
      }}
    >
      {position && (
        <div>
          X: {position.x}
          Y: {position.y}
        </div>
      )}
    </div>
  );
}

export default SignatureCanvas;