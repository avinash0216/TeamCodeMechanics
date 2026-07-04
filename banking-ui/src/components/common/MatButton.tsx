import Button from '@mui/material/Button';
import SendIcon from '@mui/icons-material/Send';
import { ReactNode } from 'react';


export default function MatButton({ title, fullWidth = false, icon, handleButtonClick }: IButtontitle) {
    return (
        <Button
      variant="contained"
      startIcon={ icon }
      fullWidth={fullWidth}
      onClick={() => {
        handleButtonClick(title);
      }}
    >
      {title}
    </Button>
    );
}

export interface IButtontitle {
    title: string;
    fullWidth?: boolean;
    icon?: ReactNode;
    iconPosition?: 'start' | 'end';
    handleButtonClick: (title: string) => void;
}