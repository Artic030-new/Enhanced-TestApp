using EnhancedTestApp.Infrastructure.Commands;
using EnhancedTestApp.ViewModels.Base;
using System;
using System.Collections.Generic;
using System.Text;
using System.Windows;
using System.Windows.Input;

namespace EnhancedTestApp.ViewModels
{
    internal class MainWindowViewModel : ViewModel
    {
        public MainWindowViewModel() 
        {
            #region === Команды ===
            CloseAppCommand = new LambdaCommand(OnCloseAppCommandExecuted, CanCloseAppCommandExecute);
            #endregion === Команды ===
        }
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

        #region === Команды ===

        #region === Завершение работы ===
        public ICommand CloseAppCommand { get; }
        private void OnCloseAppCommandExecuted(object o) => Application.Current.Shutdown();
        private bool CanCloseAppCommandExecute(object o) =>  true;
        #endregion === Завершение работы ===

        #endregion === Команды ===
    }
}
