import type { FileMetadata } from "./file";
import type { DirectoryDto } from "./directory";

export type ObjectItem =
  | { type: 'file'; data: FileMetadata }
  | { type: 'dir'; data: DirectoryDto };