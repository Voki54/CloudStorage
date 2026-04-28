import {
  File,
  Image,
  Film,
  Music,
  FileText,
  FolderArchive,
  Table,
  Presentation,
} from "lucide-react";

function getFileType(fileName?: string, contentType?: string): string {
  if (contentType) {
    if (contentType.startsWith("image/")) return "image";
    if (contentType.startsWith("video/")) return "video";
    if (contentType.startsWith("audio/")) return "audio";
    if (contentType === "application/pdf") return "pdf";
    if (contentType.includes("zip")) return "archive";
  }

  if (!fileName) return "file";

  const ext = fileName.split(".").pop()?.toLowerCase();

  switch (ext) {
    case "jpg":
    case "jpeg":
    case "png":
    case "gif":
    case "webp":
      return "image";

    case "mp4":
    case "mov":
    case "avi":
      return "video";

    case "mp3":
    case "wav":
      return "audio";

    case "pdf":
      return "pdf";

    case "zip":
    case "rar":
    case "7z":
      return "archive";

    case "doc":
    case "docx":
      return "word";

    case "xls":
    case "xlsx":
      return "excel";

    case "ppt":
    case "pptx":
      return "presentation";

    case "txt":
      return "text";

    default:
      return "file";
  }
}

type Props = {
  fileName?: string;
  contentType?: string;
  className?: string;
};

export const FileIcon = ({ fileName, contentType, className }: Props) => {
  const type = getFileType(fileName, contentType);

  switch (type) {
    case "image":
      return <Image className={className} />;

    case "video":
      return <Film className={className} />;

    case "audio":
      return <Music className={className} />;

    case "pdf":
      return <FileText className={`${className}`} />;

    case "archive":
      return <FolderArchive className={className} />;

    case "word":
      return <FileText className={` ${className}`} />;

    case "excel":
      return <Table className={` ${className}`} />;

    case "presentation":
      return <Presentation className={`${className}`} />;

    case "text":
      return <FileText className={className} />;

    default:
      return <File className={className} />;
  }
};