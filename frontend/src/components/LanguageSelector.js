import React from 'react';
import { 
  IconButton, 
  Menu, 
  MenuItem, 
  ListItemIcon, 
  ListItemText,
  Tooltip
} from '@material-ui/core';
import { Language as LanguageIcon } from '@material-ui/icons';
import { makeStyles } from '@material-ui/core/styles';
import { useI18n } from '../contexts/I18nContext';

const useStyles = makeStyles((theme) => ({
  languageButton: {
    color: 'inherit',
    marginLeft: theme.spacing(1),
  },
  flag: {
    width: 24,
    height: 18,
    marginRight: theme.spacing(1),
    fontSize: 18,
  },
  activeLanguage: {
    backgroundColor: theme.palette.action.selected,
  },
}));

const LanguageSelector = () => {
  const classes = useStyles();
  const { locale, switchLanguage, t } = useI18n();
  const [anchorEl, setAnchorEl] = React.useState(null);

  const handleClick = (event) => {
    setAnchorEl(event.currentTarget);
  };

  const handleClose = () => {
    setAnchorEl(null);
  };

  const handleLanguageChange = (lang) => {
    switchLanguage(lang);
    handleClose();
  };

  const languages = [
    { code: 'ja', name: '日本語', flag: '🇯🇵' },
    { code: 'en', name: 'English', flag: '🇺🇸' },
  ];

  return (
    <>
      <Tooltip title={t('app.language', 'Language')}>
        <IconButton
          className={classes.languageButton}
          onClick={handleClick}
          aria-label="language selector"
        >
          <LanguageIcon />
        </IconButton>
      </Tooltip>
      <Menu
        anchorEl={anchorEl}
        keepMounted
        open={Boolean(anchorEl)}
        onClose={handleClose}
        PaperProps={{
          style: {
            minWidth: 150,
          },
        }}
      >
        {languages.map((language) => (
          <MenuItem
            key={language.code}
            onClick={() => handleLanguageChange(language.code)}
            className={locale === language.code ? classes.activeLanguage : ''}
          >
            <ListItemIcon>
              <span className={classes.flag}>{language.flag}</span>
            </ListItemIcon>
            <ListItemText primary={language.name} />
          </MenuItem>
        ))}
      </Menu>
    </>
  );
};

export default LanguageSelector;