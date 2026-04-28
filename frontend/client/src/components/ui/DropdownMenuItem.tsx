interface Props {
  label: string;
  onClick?: () => void;
}

export default function DropdownMenuItem({ label, onClick }: Props) {
  return (
    <button
      onClick={onClick}
      className="w-full text-left px-4 py-1.5 text-sm hover:bg-gray-100 transition"
    >
      {label}
    </button>
  );
}