import type { LucideIcon } from "lucide-react";

type IconButtonProps = {
  icon: LucideIcon;
  onClick: () => void;
  size?: number;
  variant?: "default" | "danger";
};

export const IconButton = ({
  icon: Icon,
  onClick,
  size = 4,
  variant = "default",
}: IconButtonProps) => {
  const base = "p-2 rounded-xl transition-colors";
  const styles = {
    default: "hover:bg-gray-200 text-gray-600",
    danger: "hover:bg-red-100 text-red-600",
  };

  return (
    <button onClick={onClick} className={`${base} ${styles[variant]}`}> <Icon className={`w-${size} h-${size}`} /> </button>
  );
};
