using EnhancedTestApp.ViewModels.Base;
using System;
using System.Collections.Generic;
using System.Text;

namespace EnhancedTestApp.ViewModels
{
    internal class MainWindowViewModel : ViewModel
    {
        #region Свойство заголовка
        /// <summary> Заголовок окна </summary>
        private string _Title = "Главное меню";
        public string Title
        {
            get => _Title;
            set => Set(ref _Title, value);
        }
        #endregion

        #region Свойство статус
        /// <summary> Заголовок окна </summary>
        private string _Status = "Something...";
        public string Status
        {
            get => _Status;
            set => Set(ref _Status, value);
        }
        #endregion

    }
}
