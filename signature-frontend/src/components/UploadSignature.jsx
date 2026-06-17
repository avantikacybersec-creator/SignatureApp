import { useState } from "react";
import api from "../services/api";

function UploadSignature() {

  const [file, setFile] = useState(null);

  const handleUpload = async () => {

    const formData = new FormData();
    formData.append("file", file);

    try {

      const response = await api.post(
        "/signatures/upload",
        formData
      );

      alert(response.data);

    } catch (error) {

      console.log(error);
      alert("Upload Failed");
    }
  };

  return (
    <div>

      <h2>Upload Signature</h2>

      <input
        type="file"
        onChange={(e) =>
          setFile(e.target.files[0])
        }
      />

      <button onClick={handleUpload}>
        Upload Signature
      </button>

    </div>
  );
}

export default UploadSignature;