import { useState } from "react";
import api from "../services/api";

function PdfViewer() {

  const [documents, setDocuments] = useState([]);

  const loadDocuments = async () => {

    try {

      const response = await api.get("/documents");

      console.log(response.data);

      setDocuments(response.data);

    } catch (error) {

      console.log(error);
      alert("Failed to load documents");
    }
  };

  return (
    <div>

      <h2>PDF Viewer</h2>

      <button onClick={loadDocuments}>
        Load Documents
      </button>

      <ul>
        {documents.map((doc) => (
          <li key={doc.id}>
            {doc.fileName}
          </li>
        ))}
      </ul>

    </div>
  );
}

export default PdfViewer;