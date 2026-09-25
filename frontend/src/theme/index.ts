import { createTheme } from '@mui/material/styles';

/**
 * Casa Carmona Design System — PSA Frontend
 * Codex Heraldicus Domus Carmona v1.0
 *
 * Pilares: DARKNESS_FIRST | SINGLE_LIGHT_SOURCE | LUXURY_THROUGH_SIMPLICITY
 */
export const carmonaTheme = createTheme({
  palette: {
    mode: 'dark',
    background: {
      default: '#0b0b0d',
      paper: '#111115',
    },
    primary: {
      main: '#c9a96e',
      light: '#d9bf91',
      dark: '#a88a52',
      contrastText: '#0b0b0d',
    },
    secondary: {
      main: '#4a4a56',
      contrastText: '#f0ede8',
    },
    divider: '#2a2a32',
    text: {
      primary: '#f0ede8',
      secondary: '#8a8a96',
      disabled: '#4a4a56',
    },
  },
  typography: {
    fontFamily: "'Montserrat', 'Helvetica Neue', 'Arial', sans-serif",
    h1: {
      fontFamily: "'Cormorant Garamond', 'Garamond', serif",
      fontWeight: 600,
      textTransform: 'uppercase',
      letterSpacing: '0.12em',
    },
    h2: {
      fontFamily: "'Cormorant Garamond', 'Garamond', serif",
      fontWeight: 500,
      textTransform: 'uppercase',
      letterSpacing: '0.1em',
    },
    h3: {
      fontFamily: "'Cormorant Garamond', 'Garamond', serif",
      fontWeight: 500,
      textTransform: 'uppercase',
      letterSpacing: '0.08em',
    },
    h4: {
      fontFamily: "'Cormorant Garamond', 'Garamond', serif",
      fontWeight: 400,
      textTransform: 'uppercase',
      letterSpacing: '0.06em',
    },
    h5: { fontFamily: "'Montserrat', sans-serif", fontWeight: 500 },
    h6: { fontFamily: "'Montserrat', sans-serif", fontWeight: 500 },
    body1: { fontFamily: "'Montserrat', sans-serif", fontWeight: 300, lineHeight: 1.7 },
    body2: { fontFamily: "'Montserrat', sans-serif", fontWeight: 300, lineHeight: 1.6 },
    overline: {
      fontFamily: "'JetBrains Mono', 'Courier New', monospace",
      color: '#c9a96e',
      letterSpacing: '0.15em',
    },
    caption: {
      fontFamily: "'JetBrains Mono', 'Courier New', monospace",
      color: '#8a8a96',
      fontSize: '0.7rem',
    },
  },
  shape: { borderRadius: 2 },
  components: {
    MuiCssBaseline: {
      styleOverrides: `
        @import url('https://fonts.googleapis.com/css2?family=Cormorant+Garamond:wght@300;400;500;600&family=Montserrat:wght@300;400;500&family=JetBrains+Mono:wght@400&display=swap');
        body { background-color: #0b0b0d; color: #f0ede8; }
        ::-webkit-scrollbar { width: 6px; }
        ::-webkit-scrollbar-track { background: #0b0b0d; }
        ::-webkit-scrollbar-thumb { background: #2a2a32; border-radius: 2px; }
        ::-webkit-scrollbar-thumb:hover { background: #4a4a56; }
      `,
    },
    MuiPaper: {
      styleOverrides: {
        root: { backgroundImage: 'none', border: '1px solid #2a2a32' },
      },
    },
    MuiTableHead: {
      styleOverrides: {
        root: {
          '& .MuiTableCell-head': {
            backgroundColor: '#111115',
            color: '#c9a96e',
            fontFamily: "'JetBrains Mono', monospace",
            fontSize: '0.7rem',
            letterSpacing: '0.12em',
            textTransform: 'uppercase',
            borderBottom: '1px solid #c9a96e',
          },
        },
      },
    },
    MuiTableRow: {
      styleOverrides: {
        root: {
          '&:hover': { backgroundColor: '#111115' },
          '& .MuiTableCell-body': { borderBottom: '1px solid #2a2a32', color: '#f0ede8' },
        },
      },
    },
    MuiButton: {
      styleOverrides: {
        root: {
          borderRadius: 2,
          textTransform: 'uppercase',
          letterSpacing: '0.08em',
          fontFamily: "'Montserrat', sans-serif",
          fontWeight: 500,
          fontSize: '0.75rem',
        },
      },
    },
    MuiChip: {
      styleOverrides: {
        root: {
          fontFamily: "'JetBrains Mono', monospace",
          fontSize: '0.65rem',
          letterSpacing: '0.08em',
          borderRadius: 2,
        },
      },
    },
    MuiTextField: {
      styleOverrides: {
        root: {
          '& .MuiOutlinedInput-root': {
            '& fieldset': { borderColor: '#2a2a32' },
            '&:hover fieldset': { borderColor: '#4a4a56' },
            '&.Mui-focused fieldset': { borderColor: '#c9a96e' },
          },
        },
      },
    },
    MuiDivider: { styleOverrides: { root: { borderColor: '#2a2a32' } } },
    MuiAppBar: {
      styleOverrides: {
        root: {
          backgroundColor: '#0b0b0d',
          borderBottom: '1px solid #2a2a32',
          backgroundImage: 'none',
          boxShadow: 'none',
        },
      },
    },
    MuiDrawer: {
      styleOverrides: {
        paper: {
          backgroundColor: '#0b0b0d',
          borderRight: '1px solid #2a2a32',
        },
      },
    },
  },
});
