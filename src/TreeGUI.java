import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.event.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.tree.*;

/**
 * Класс графического интерфейса дерева для реализации объектов семейного древа и членов семьи.
 * В основном отображает способ взаимодействия с объектами генеалогического древа
 * Графический интерфейс разделен на 4 основных раздела.
 * о Строка меню
 * - содержит опции меню
 * o Заголовочная панель
 * - содержит кнопки загрузки, сохранения и создания нового дерева
 * о Панель управления
 * - содержит jTree-представление древовидного объекта и панели сведений
 * который содержит информацию о текущем члене или добавляет и редактирует формы
 * о Панель состояния
 * - содержит сообщение о состоянии
 * Предположения:
 * Должны быть классы FamilyTree и FamilyMember
 * Пользователь будет взаимодействовать с этой программой с помощью мыши и клавиатуры
 * Английский является единственным поддерживаемым языком
 */
public class TreeGUI {

    /**
     * Создает и настраивает графический интерфейс, а также инициализирует все переменные.
     */
    public TreeGUI() {

        currentFamilyTree = new FamilyTree();
        currentFile = null;
        tree = new JTree();
        createGUI();
    }
    private JFrame mainFrame;
    private JPanel controlPanel;
    private JPanel infoPanel;
    private final JLabel statusLabel = new JLabel("Программа загружена");
    private File currentFile;
    private JTree tree;

    private FamilyTree currentFamilyTree;

    /**
     * Вызывает функции инициализации для настройки всех различных панелей.
     */
    private void createGUI() {

        mainFrame = new JFrame("Генеалогическое дерево");
        mainFrame.setSize(600, 800);
        mainFrame.setLayout(new BorderLayout());
        mainFrame.getContentPane().setBackground(Color.WHITE);
        mainFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        initMenuBar();

        initHeaderPanel();

        initControlPanel();

        initStatusBar();

        displayTree(currentFamilyTree);

        mainFrame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent windowEvent) {
                if (checkUserContinue()) {
                    System.exit(0);
                }
            }
        });

        mainFrame.setVisible(true);
    }

    private void initHeaderPanel() {

        JLabel headerLabel = new JLabel("Добро пожаловать в приложение ГЕНЕАЛОГИЧЕСКОЕ ДЕРЕВО", JLabel.LEFT);
        headerLabel.setFont(new Font("SansSerif", Font.PLAIN, 28));

        JButton open = new JButton("Загрузить дерево");
        open.addActionListener(new openAction());

        JButton create = new JButton("Создать новое дерево");
        create.addActionListener(new createTreeAction());

        JButton saveTree = new JButton("Сохранить дерево");
        saveTree.addActionListener(new saveAction());

        JPanel headPanel = new JPanel();
        headPanel.setLayout(new GridBagLayout());
        headPanel.setOpaque(false);
        headPanel.setBorder(new EmptyBorder(0,10,10,10));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        headPanel.add(headerLabel, gbc);

        JPanel container = new JPanel(new FlowLayout(FlowLayout.LEFT));
        container.setOpaque(false);
        container.add(open);
        container.add(saveTree);
        container.add(create);

        gbc.gridx = 0;
        gbc.gridy = 1;
        headPanel.add(container, gbc);

        mainFrame.add(headPanel, BorderLayout.NORTH);
    }

    /**
     * Инициализирует панель управления, где отображается основная часть данных
     */
    private void initControlPanel() {
        controlPanel = new JPanel();

        controlPanel.setOpaque(false);
        controlPanel.setLayout(new BoxLayout(controlPanel, BoxLayout.Y_AXIS));

        mainFrame.add(controlPanel, BorderLayout.CENTER);
    }

    /**
     * Инициализировать строку меню, содержащую такие действия меню, как сохранение, загрузка нового и выход.
     */
    private void initMenuBar() {
        JMenuBar menuBar;
        menuBar = new JMenuBar();
        mainFrame.setJMenuBar(menuBar);

        JMenu fileMenu = new JMenu("Файл");
        menuBar.add(fileMenu);

        JMenuItem newAction = new JMenuItem("Новый");
        fileMenu.add(newAction);
        newAction.addActionListener(new createTreeAction());

        JMenuItem openAction = new JMenuItem("Открыть");
        fileMenu.add(openAction);
        openAction.addActionListener(new openAction());

        fileMenu.addSeparator();

        JMenuItem saveAction = new JMenuItem("Сохранить");
        fileMenu.add(saveAction);
        saveAction.addActionListener(new saveAction());

        JMenuItem saveAsAction = new JMenuItem("Сохранить как");
        fileMenu.add(saveAsAction);
        saveAsAction.addActionListener(new saveAsAction());


        JMenuItem exitAction = new JMenuItem("Выход");
        fileMenu.addSeparator();
        fileMenu.add(exitAction);
        exitAction.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (checkUserContinue()) {
                    System.exit(0);
                }
            }
        });
    }

    /**
     * Инициализирует строку состояния, в которой отображается такая информация, как сообщения.
     * отображается пользователю в самом низу экрана
     */
    private void initStatusBar() {
        JPanel statusPanel = new JPanel();
        statusPanel.setBorder(new BevelBorder(BevelBorder.LOWERED));

        mainFrame.add(statusPanel, BorderLayout.SOUTH);

        statusPanel.setPreferredSize(new Dimension(mainFrame.getWidth(), 18));
        statusPanel.setLayout(new BoxLayout(statusPanel, BoxLayout.X_AXIS));

        statusLabel.setHorizontalAlignment(SwingConstants.LEFT);
        statusPanel.add(statusLabel);
    }

    /**
     * Удобный способ редактирования статуса. В основном задает текст
     * метка внутри строки состояния
     * параметр status сообщение для отображения
     */
    private void editStatus(String status) {
        statusLabel.setText(status);
    }

    /**
     * Класс действий, который реализует ActionListner
     * Используется для отображения функции добавления относительного значения после нажатия кнопки для указанного
     * член семьи
     */
    private class addRelativeAction implements ActionListener {

        private FamilyMember member;
        public addRelativeAction(FamilyMember member) {
            this.member = member;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            displayAddRelativeInfo(member);
        }
    }

    /**
     * Изменить действие участника, которое реализует ActionListner для отображения
     * форма редактирования члена при нажатии кнопки для указанного члена семьи
     */
    private class editMemberAction implements ActionListener {

        private FamilyMember member;
        public editMemberAction(FamilyMember member) {
            this.member = member;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            displayEditMemberInfo(member);
        }
    }

    /**
     * действие создания дерева реализует список действий для отображения формы создания дерева
     * для указанного члена семьи
     */
    private class createTreeAction implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {

            if (checkUserContinue()) {

                currentFamilyTree = new FamilyTree();
                currentFile = null;
                displayTree(currentFamilyTree);
                editStatus("Создано пустое дерево");
            }

        }
    }

    /**
     * Действие Open реализует список действий, который вызывает jDialogBox таким образом, что
     * пользователь может выбрать файл для открытия в приложении
     */
    private class openAction implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            if (checkUserContinue()) {
                JFileChooser jFileChooser = new JFileChooser();
                jFileChooser.addChoosableFileFilter(new FileNameExtensionFilter("FamilyTree Files (*.ft)", "ft"));
                jFileChooser.setAcceptAllFileFilterUsed(true);

                int result = jFileChooser.showOpenDialog(mainFrame);
                if (result == JFileChooser.APPROVE_OPTION) {
                    try {
                        openFile(jFileChooser.getSelectedFile());
                        displayTree(currentFamilyTree);
                        editStatus("Файл открыт из: " + (jFileChooser.getSelectedFile().getAbsolutePath()));
                    }
                    catch (Exception j) {
                        showErrorDialog(j);
                        editStatus("Ошибка: " + j.getMessage());
                    }
                }
            }

        }
    }

    /**
     * Удобный метод проверки загруженности дерева. Используется для проверки того, что пользователь
     * хочет продолжить, несмотря на загрузку дерева
     * вернуть true, если у дерева нет корня или если пользователь хочет продолжить
     */
    private boolean checkUserContinue() {
        if (currentFamilyTree.hasRoot()) {
            int dialogResult = JOptionPane.showConfirmDialog(mainFrame, "Вы уверены, что хотите продолжить? Любые несохраненные изменения будут потеряны" , "Предупреждение", JOptionPane.YES_NO_CANCEL_OPTION);
            return dialogResult == JOptionPane.YES_OPTION;
        }
        return true;
    }

    /**
     * отображает объект генеалогического дерева через jTree.
     * параметр familyTree генеалогическое древо для отображения
     */
    private void displayTree(FamilyTree familyTree) {

        DefaultMutableTreeNode main = new DefaultMutableTreeNode("Главный");
        TreePath lastSelectedNode = null;

        DefaultMutableTreeNode top;

        if (!familyTree.hasRoot()) {
            top = new DefaultMutableTreeNode("Данные дерева не найдены.");

        }
        else {
            top = new DefaultMutableTreeNode(familyTree.getRoot());
            createTree(top, familyTree.getRoot());
            lastSelectedNode = tree.getSelectionPath();

        }
        tree = new JTree(main);
        main.add(top);
        tree.setRootVisible(false);
        tree.setShowsRootHandles(true);
        tree.setEnabled(true);
        tree.expandPath(new TreePath(main.getPath()));
        tree.getSelectionModel().addTreeSelectionListener(new treeSelectorAction());
        tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        tree.setBorder(new EmptyBorder(0, 10, 0, 10));

        for (int i = 0; i < tree.getRowCount(); i++) {
            tree.expandRow(i);
        }

        tree.setCellRenderer(new DefaultTreeCellRenderer() {
            @Override
            public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean exp, boolean leaf, int row, boolean hasFocus) {

                DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
                Object nodeInfo = node.getUserObject();
                if (nodeInfo instanceof FamilyMember) {
                    setTextNonSelectionColor(Color.BLACK);
                    setBackgroundSelectionColor(Color.LIGHT_GRAY);
                    setTextSelectionColor(Color.BLACK);
                    setBorderSelectionColor(Color.WHITE);
                }
                else {
                    setTextNonSelectionColor(Color.GRAY);
                    setBackgroundSelectionColor(Color.WHITE);
                    setTextSelectionColor(Color.GRAY);
                    setBorderSelectionColor(Color.WHITE);
                }
                setLeafIcon(null);
                setClosedIcon(null);
                setOpenIcon(null);
                super.getTreeCellRendererComponent(tree, value, sel, exp, leaf, row, hasFocus);
                return this;
            }
        });

        JScrollPane treeScrollPane = new JScrollPane(tree);
        treeScrollPane.setPreferredSize(new Dimension(250, 0));

        infoPanel = new JPanel();
        infoPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));

        JLabel promptInfo;
        JButton addNewRoot = new JButton("Добавить пользователя");
        addNewRoot.addActionListener(new addRelativeAction(null));
        if (!familyTree.hasRoot()) {
            promptInfo = new JLabel("<html>Загрузить дерево или добавить нового пользователя</html>");
            infoPanel.add(addNewRoot);
        } else {
            promptInfo = new JLabel("<html>Выберите члена семьи для просмотра информации</html>");
        }

        promptInfo.setFont(new Font("SansSerif", Font.PLAIN, 20));
        infoPanel.add(promptInfo, BorderLayout.NORTH);
        infoPanel.setOpaque(false);

        controlPanel.removeAll();

        JPanel container = new JPanel(new FlowLayout(FlowLayout.LEFT));
        controlPanel.add(container);

        container.setLayout(new BorderLayout());
        container.add(treeScrollPane, BorderLayout.WEST);
        container.add(infoPanel, BorderLayout.CENTER);

        controlPanel.add(container);
        controlPanel.validate();
        controlPanel.repaint();

        tree.setSelectionPath(lastSelectedNode);
    }

    /**
     * отменяет редактирование, возвращаясь к форме информации о члене
     */
    private class cancelEditMemberAction implements ActionListener {

        FamilyMember member;

        public cancelEditMemberAction(FamilyMember member) {
            this.member = member;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            displayMemberInfo(member);
            editStatus("Действие отменено");
        }
    }

    /**
     * если файл существует, предложить перезаписать сохраненный файл. Если нет, инициируйте действие «Сохранить как».
     */
    private class saveAction implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                if (currentFile != null) {
                    int dialogResult = JOptionPane.showConfirmDialog(mainFrame, "Хотели бы вы перезаписать текущее дерево?" , "Предупреждение", JOptionPane.YES_NO_OPTION);
                    if (dialogResult == JOptionPane.YES_OPTION) {
                        saveToFile(currentFile);
                        editStatus("Файл сохранен в: " + currentFile.getPath());
                    }
                } else {
                    editStatus("Файл не загружен");
                    ActionListener listner = new saveAsAction();
                    listner.actionPerformed(e);

                }

            } catch (Exception j) {
                showErrorDialog(j);
                editStatus("Ошибка: "+ j.getMessage());
            }
        }
    }

    /**
     * сохранить текущее дерево как другой файл
     */
    private class saveAsAction implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            JFileChooser jFileChooser = new JFileChooser() {
                @Override
                public void approveSelection() {
                    File selectedFile = getSelectedFile();
                    if (selectedFile.exists() && getDialogType() == SAVE_DIALOG) {
                        int result = JOptionPane.showConfirmDialog(this, "Файл существует, перезаписать?" , "Существующий файл", JOptionPane.YES_NO_CANCEL_OPTION);
                        switch (result) {
                            case JOptionPane.YES_OPTION:
                                super.approveSelection();
                                return;
                            case JOptionPane.NO_OPTION:
                                return;
                            case JOptionPane.CLOSED_OPTION:
                                return;
                            case JOptionPane.CANCEL_OPTION:
                                cancelSelection();
                                return;
                        }
                    }
                    super.approveSelection();
                }
            };
            jFileChooser.setSelectedFile(new File("Family Tree.ft"));
            jFileChooser.setFileFilter(new FileNameExtensionFilter("Файлы FamilyTree (*.ft)", "ft"));
            int result = jFileChooser.showSaveDialog(mainFrame);
            if (result == JFileChooser.APPROVE_OPTION) {
                try {
                    String filename = jFileChooser.getSelectedFile().toString();
                    if (!filename.endsWith(".ft")) {
                        filename += ".ft";
                    }
                    File file = new File(filename);

                    saveToFile(file);
                    displayTree(currentFamilyTree);
                    editStatus("Файл сохранен в: " + (file.getAbsolutePath()));
                }
                catch (Exception j) {
                    showErrorDialog(j);
                    editStatus("Ошибка: "+ j.getMessage());
                }
            }
        }
    }

    /**
     * действие вызывается, когда пользователь выбирает узел в дереве
     */
    private class treeSelectorAction implements TreeSelectionListener {

        public void valueChanged(TreeSelectionEvent event) {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();

            if (node == null) {
                return;
            }

            Object nodeInfo = node.getUserObject();
            if (nodeInfo instanceof FamilyMember) {
                displayMemberInfo((FamilyMember) nodeInfo);
                editStatus("Показать подробности для: " + ((FamilyMember) nodeInfo));
            }
        }
    }

    /**
     * Сохраняет объект в файл с помощью сериализации
     * параметр file файл для сохранения
     */
    private void saveToFile(File file) {
        // сохраняем объект в файл
        FileOutputStream fos = null;
        ObjectOutputStream out = null;
        try {
            //устанавливаем выходные потоки
            fos = new FileOutputStream(file);
            out = new ObjectOutputStream(fos);
            //записываем объект в файл
            out.writeObject(this.currentFamilyTree);

            out.close();
            currentFile = file;
        }
        catch (Exception ex) {
            throw new IllegalArgumentException("Не удалось сохранить файл");
        }
    }

    /**
     * Открывает файл и загружает данные в существующие переменные
     * параметр file файл для открытия
     */
    private void openFile(File file) {
        FileInputStream fis = null;
        ObjectInputStream in = null;
        FamilyTree ft = null;
        try {
            fis = new FileInputStream(file);
            in = new ObjectInputStream(fis);

            ft = (FamilyTree) in.readObject();
            in.close();

            currentFamilyTree.setRoot(ft.getRoot());
            currentFile = file;
            tree = new JTree();
        }
        catch (Exception ex) {
            throw new IllegalArgumentException("Файл не может быть прочитан.");
        }

    }

    /**
     * Отображает данные указанного члена
     * @param member сведения об участнике для отображения
     */
    private void displayMemberInfo(FamilyMember member) {
        tree.setEnabled(true);

        infoPanel.removeAll();
        infoPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        infoPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        JPanel container = new JPanel();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;

        infoPanel.add(container, gbc);

        GroupLayout layout = new GroupLayout(container);
        container.setLayout(layout);
        layout.setAutoCreateGaps(true);

        JLabel memberInfoLabel = new JLabel("Информация о человеке: ");
        memberInfoLabel.setFont(new Font("SansSerif", Font.PLAIN, 20));
        JLabel nameLabel = new JLabel("Имя");
        JLabel nameTextField = new JLabel(member.getFirstName(), 10);
        JLabel lastnameLabel = new JLabel("Фамилия");
        JLabel lastnameTextField = new JLabel(member.getLastName(), 10);
        JLabel maidennameLabel = new JLabel("Девичья фамилия");
        JLabel maidennameTextField = new JLabel();
        if (member.has(FamilyMember.Attribute.ДЕВИЧЬЯ_ФАМИЛИЯ)) {
            maidennameTextField.setText(member.getMaidenName());
        }
        else {
            maidennameTextField.setText("-");
        }

        JLabel genderLabel = new JLabel("Пол");
        JLabel genderComboBox = new JLabel(member.getGender().toString());

        JLabel lifeDescriptionLabel = new JLabel("Описание");
        JTextArea lifeDescriptionTextArea = new JTextArea(5, 20);
        lifeDescriptionTextArea.setText(member.getLifeDescription());
        lifeDescriptionTextArea.setWrapStyleWord(true);
        lifeDescriptionTextArea.setLineWrap(true);
        lifeDescriptionTextArea.setOpaque(false);
        lifeDescriptionTextArea.setEditable(false);
        lifeDescriptionTextArea.setFocusable(false);
        lifeDescriptionTextArea.setBackground(UIManager.getColor("Label.background"));
        lifeDescriptionTextArea.setFont(UIManager.getFont("Label.font"));
        lifeDescriptionTextArea.setBorder(UIManager.getBorder("Label.border"));
        JScrollPane lifeDescriptionScrollPane1 = new JScrollPane(lifeDescriptionTextArea);
        lifeDescriptionScrollPane1.setBorder(UIManager.getBorder("Label.border"));


        JLabel addressInfoLabel = new JLabel("Информация об адресе: ");
        addressInfoLabel.setFont(new Font("SansSerif", Font.PLAIN, 20));
        JLabel streetNoLabel = new JLabel("Номер дома: ");
        JLabel streetNoTextField = new JLabel(member.getAddress().getStreetNumber(), 10);
        JLabel streetNameLabel = new JLabel("Название улицы: ");
        JLabel streetNameTextField = new JLabel(member.getAddress().getStreetName(), 10);
        JLabel suburbLabel = new JLabel("Населённый пункт:");
        JLabel suburbTextField = new JLabel(member.getAddress().getSuburb(), 10);
        JLabel postcodeLabel = new JLabel("Почтовый индекс");
        JLabel postcodeTextField = new JLabel(member.getAddress().getPostCode() + "", 10);

        JLabel relativeInfoLabel = new JLabel("Информация о родственнике: ");
        relativeInfoLabel.setFont(new Font("SansSerif", Font.PLAIN, 20));

        JLabel fatherLabel = new JLabel("Отец");
        JLabel fatherTextField = new JLabel();
        if (member.has(FamilyMember.Attribute.ОТЕЦ)) {
            fatherTextField.setText(member.getFather().toString());
        }
        else {
            fatherTextField.setText("Нет отца в записи");
        }
        JLabel motherLabel = new JLabel("Мать");
        JLabel motherTextField = new JLabel();
        if (member.has(FamilyMember.Attribute.МАТЬ)) {
            motherTextField.setText(member.getMother().toString());
        }
        else {
            motherTextField.setText("Нет матери в записи");
        }
        JLabel spouseLabel = new JLabel("Супруг");
        JLabel spouseTextField = new JLabel();
        if (member.has(FamilyMember.Attribute.СУПРУГ)) {
            spouseTextField.setText(member.getSpouse().toString());
        }
        else {
            spouseTextField.setText("Нет супруга в записи");
        }
        JLabel childrenLabel = new JLabel("Дети");
        String children = "<html>";
        if (member.has(FamilyMember.Attribute.ДЕТИ)) {
            for (FamilyMember child : member.getChildren()) {
                children += child.toString() + "<br>";
            }
            children += "</html>";
        }
        else {
            children = "Нет детей в записи";
        }
        JLabel childrenTextField = new JLabel(children);

        JLabel grandChildrenLabel = new JLabel("Внуки");
        String grandChildren = "<html>";
        if (member.has(FamilyMember.Attribute.ДЕТИ)) {
            for (FamilyMember child : member.getChildren()) {
                if (child.has(FamilyMember.Attribute.ДЕТИ)) {
                    for (FamilyMember grandChild : child.getChildren()) {
                        grandChildren += grandChild.toString() + "<br>";
                    }
                }

            }
            grandChildren += "</html>";
        }
        else {
            grandChildren = "Зарегистрировано отсутствие внуков";
        }
        JLabel grandChildrenTextField = new JLabel(grandChildren);

        layout.setHorizontalGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addComponent(memberInfoLabel)
                        .addComponent(nameLabel)
                        .addComponent(lastnameLabel)
                        .addComponent(maidennameLabel)
                        .addComponent(genderLabel)
                        .addComponent(lifeDescriptionLabel)
                        .addComponent(addressInfoLabel)
                        .addComponent(streetNoLabel)
                        .addComponent(streetNameLabel)
                        .addComponent(suburbLabel)
                        .addComponent(postcodeLabel)
                        .addComponent(relativeInfoLabel)
                        .addComponent(fatherLabel)
                        .addComponent(motherLabel)
                        .addComponent(spouseLabel)
                        .addComponent(childrenLabel)
                        .addComponent(grandChildrenLabel)
                )
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addComponent(nameTextField)
                        .addComponent(lastnameTextField)
                        .addComponent(maidennameTextField)
                        .addComponent(lifeDescriptionScrollPane1)
                        .addComponent(genderComboBox)
                        .addComponent(addressInfoLabel)
                        .addComponent(streetNoTextField)
                        .addComponent(streetNameTextField)
                        .addComponent(suburbTextField)
                        .addComponent(postcodeTextField)
                        .addComponent(fatherTextField)
                        .addComponent(motherTextField)
                        .addComponent(spouseTextField)
                        .addComponent(childrenTextField)
                        .addComponent(grandChildrenTextField)
                )
        );

        layout.setVerticalGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(memberInfoLabel))
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(nameLabel)
                        .addComponent(nameTextField))
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(lastnameLabel)
                        .addComponent(lastnameTextField))
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(maidennameLabel)
                        .addComponent(maidennameTextField))
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(genderLabel)
                        .addComponent(genderComboBox))
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(lifeDescriptionLabel)
                        .addComponent(lifeDescriptionScrollPane1))
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(addressInfoLabel))
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(streetNoLabel)
                        .addComponent(streetNoTextField))
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(streetNameLabel)
                        .addComponent(streetNameTextField))
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(suburbLabel)
                        .addComponent(suburbTextField))
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(postcodeLabel)
                        .addComponent(postcodeTextField))
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(relativeInfoLabel))
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(fatherLabel)
                        .addComponent(fatherTextField))
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(motherLabel)
                        .addComponent(motherTextField))
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(spouseLabel)
                        .addComponent(spouseTextField))
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(childrenLabel)
                        .addComponent(childrenTextField))
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(grandChildrenLabel)
                        .addComponent(grandChildrenTextField))
        );

        JButton editMember = new JButton("Редактировать детали");
        editMember.addActionListener(new editMemberAction(member));
        JButton addRelative = new JButton("Добавить общую информацию");
        addRelative.addActionListener(new addRelativeAction(member));

        JPanel btncontainer = new JPanel(new FlowLayout(FlowLayout.LEFT));
        btncontainer.add(editMember);
        btncontainer.add(addRelative);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        infoPanel.add(btncontainer, gbc);
        infoPanel.validate();
        infoPanel.repaint();
    }

    /**
     * Отображает форму редактирования участника
     * @param member он член для редактирования
     */
    private void displayEditMemberInfo(FamilyMember member) {
        tree.setEnabled(false);

        infoPanel.removeAll();
        infoPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        // Создаем макет
        JPanel info = new JPanel();
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;

        infoPanel.add(info, gbc);
        GroupLayout layout = new GroupLayout(info);
        info.setLayout(layout);
        layout.setAutoCreateGaps(true);

        JLabel memberInfoLabel = new JLabel("Информация о человеке: ");
        memberInfoLabel.setFont(new Font("SansSerif", Font.PLAIN, 20));
        JLabel nameLabel = new JLabel("Имя");
        JTextField nameTextField = new JTextField(member.getFirstName(), 10);
        JLabel lastnameLabel = new JLabel("Фамилия");
        JTextField lastnameTextField = new JTextField(member.getLastName(), 10);
        JLabel maidennameLabel = new JLabel("Девичья фамилия");
        JTextField maidennameTextField = new JTextField(member.getMaidenName(), 10);
        if (member.getGender() != FamilyMember.Gender.ЖЕНСКИЙ) {
            maidennameTextField.setEditable(false);
        }
        JLabel genderLabel = new JLabel("Пол");
        DefaultComboBoxModel<FamilyMember.Gender> genderList = new DefaultComboBoxModel<>();
        genderList.addElement(FamilyMember.Gender.ЖЕНСКИЙ);
        genderList.addElement(FamilyMember.Gender.МУЖСКОЙ);
        JComboBox<FamilyMember.Gender> genderComboBox = new JComboBox<>(genderList);
        genderComboBox.setSelectedItem(member.getGender());
        genderComboBox.setEnabled(false);

        JLabel lifeDescriptionLabel = new JLabel("Описание ");
        JTextArea lifeDescriptionTextArea = new JTextArea(member.getLifeDescription(), 10, 10);
        lifeDescriptionTextArea.setLineWrap(true);
        lifeDescriptionTextArea.setWrapStyleWord(true);
        JScrollPane lifeDescriptionScrollPane1 = new JScrollPane(lifeDescriptionTextArea);

        JLabel addressInfoLabel = new JLabel("Информация об адресе: ");
        addressInfoLabel.setFont(new Font("SansSerif", Font.PLAIN, 20));
        JLabel streetNoLabel = new JLabel("Номер дома:");
        JTextField streetNoTextField = new JTextField(member.getAddress().getStreetNumber(), 10);
        JLabel streetNameLabel = new JLabel("Название улицы:");
        JTextField streetNameTextField = new JTextField(member.getAddress().getStreetName(), 10);
        JLabel suburbLabel = new JLabel("Населённый пункт:");
        JTextField suburbTextField = new JTextField(member.getAddress().getSuburb(), 10);
        JLabel postcodeLabel = new JLabel("Почтовый индекс");
        JTextField postcodeTextField = new JTextField(member.getAddress().getPostCode() + "", 10);

        layout.setHorizontalGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addComponent(memberInfoLabel)
                        .addComponent(nameLabel)
                        .addComponent(lastnameLabel)
                        .addComponent(maidennameLabel)
                        .addComponent(genderLabel)
                        .addComponent(lifeDescriptionLabel)
                        .addComponent(addressInfoLabel)
                        .addComponent(streetNoLabel)
                        .addComponent(streetNameLabel)
                        .addComponent(suburbLabel)
                        .addComponent(postcodeLabel)
                )
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addComponent(nameTextField)
                        .addComponent(lastnameTextField)
                        .addComponent(maidennameTextField)
                        .addComponent(lifeDescriptionScrollPane1)
                        .addComponent(genderComboBox)
                        .addComponent(addressInfoLabel)
                        .addComponent(streetNoTextField)
                        .addComponent(streetNameTextField)
                        .addComponent(suburbTextField)
                        .addComponent(postcodeTextField)
                )
        );

        layout.setVerticalGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(memberInfoLabel))
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(nameLabel)
                        .addComponent(nameTextField))
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(lastnameLabel)
                        .addComponent(lastnameTextField))
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(maidennameLabel)
                        .addComponent(maidennameTextField))
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(genderLabel)
                        .addComponent(genderComboBox))
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(lifeDescriptionLabel)
                        .addComponent(lifeDescriptionScrollPane1))
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(addressInfoLabel))
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(streetNoLabel)
                        .addComponent(streetNoTextField))
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(streetNameLabel)
                        .addComponent(streetNameTextField))
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(suburbLabel)
                        .addComponent(suburbTextField))
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(postcodeLabel)
                        .addComponent(postcodeTextField))
        );
        JButton saveMember = new JButton("Сохранить детали");
        saveMember.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    //пытаемся сохранить детали
                    member.setFirstName(nameTextField.getText().trim());
                    member.setLastName(lastnameTextField.getText().trim());
                    member.setMaidenName(maidennameTextField.getText().trim());
                    member.setLifeDescription(lifeDescriptionTextArea.getText().trim());
                    member.setGender((FamilyMember.Gender) genderComboBox.getSelectedItem());

                    member.getAddress().setStreetNumber(streetNoTextField.getText().trim());
                    member.getAddress().setStreetName(streetNameTextField.getText().trim());
                    member.getAddress().setSuburb(suburbTextField.getText().trim());
                    member.getAddress().setPostCode(postcodeTextField.getText().trim());
                    displayTree(currentFamilyTree);
                    editStatus("Член "+member.toString()+" добавлен");
                }
                catch (Exception d) {
                    showErrorDialog(d);
                }
            }
        });
        JButton cancel = new JButton("Отмена");
        cancel.addActionListener(new cancelEditMemberAction(member));

        JPanel container = new JPanel(new FlowLayout(FlowLayout.LEFT));
        container.setOpaque(false);
        container.add(saveMember);
        container.add(cancel);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        infoPanel.add(container, gbc);

        infoPanel.validate();
        infoPanel.repaint();
    }

    /**
     * отображать форму добавления относительного члена
     * @param member член для добавления родственника
     */
    private void displayAddRelativeInfo(FamilyMember member) {
        tree.setEnabled(false);

        infoPanel.removeAll();
        infoPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JPanel info = new JPanel();
        JLabel memberInfoLabel = new JLabel("Добавить нового пользователя", SwingConstants.LEFT);
        if (member != null) {
            memberInfoLabel.setText("Добавить родственника для " + member.toString());
        }

        memberInfoLabel.setFont(new Font("SansSerif", Font.PLAIN, 20));

        infoPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        infoPanel.add(memberInfoLabel, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.BOTH;
        infoPanel.add(info, gbc);
        // Создаем макет
        GroupLayout layout = new GroupLayout(info);
        info.setLayout(layout);
        layout.setAutoCreateGaps(true);

        JLabel relativeTypeLabel = new JLabel("Тип родственника");
        DefaultComboBoxModel<FamilyMember.RelativeType> relativeTypeList = new DefaultComboBoxModel<>();

        relativeTypeList.addElement(FamilyMember.RelativeType.МАТЬ);
        relativeTypeList.addElement(FamilyMember.RelativeType.ОТЕЦ);
        relativeTypeList.addElement(FamilyMember.RelativeType.СУПРУГ);
        relativeTypeList.addElement(FamilyMember.RelativeType.РЕБЁНОК);
        JComboBox<FamilyMember.RelativeType> relativeTypeComboBox = new JComboBox<>(relativeTypeList);

        if (member == null) {

            relativeTypeComboBox.removeAllItems();
            relativeTypeComboBox.setEnabled(false);

        }

        JLabel nameLabel = new JLabel("Имя");
        JTextField nameTextField = new JTextField("", 10);
        JLabel lastnameLabel = new JLabel("Фамилия");
        JTextField lastnameTextField = new JTextField("", 10);

        JLabel maidennameLabel = new JLabel("Девичья фамилия");
        JTextField maidennameTextField = new JTextField(10);

        JLabel genderLabel = new JLabel("Пол");
        DefaultComboBoxModel<FamilyMember.Gender> genderList = new DefaultComboBoxModel<>();
        genderList.addElement(FamilyMember.Gender.ЖЕНСКИЙ);
        genderList.addElement(FamilyMember.Gender.МУЖСКОЙ);
        JComboBox<FamilyMember.Gender> genderComboBox = new JComboBox<>(genderList);

        JLabel lifeDescriptionLabel = new JLabel("Описание");
        JTextArea lifeDescriptionTextArea = new JTextArea(10, 10);
        lifeDescriptionTextArea.setLineWrap(true);
        lifeDescriptionTextArea.setWrapStyleWord(true);
        JScrollPane lifeDescriptionScrollPane1 = new JScrollPane(lifeDescriptionTextArea);

        JLabel addressInfoLabel = new JLabel("Информация об адресе:");
        addressInfoLabel.setFont(new Font("SansSerif", Font.PLAIN, 20));
        JLabel streetNoLabel = new JLabel("Номер дома:");
        JTextField streetNoTextField = new JTextField("", 10);
        JLabel streetNameLabel = new JLabel("Название улицы:");
        JTextField streetNameTextField = new JTextField("", 10);
        JLabel suburbLabel = new JLabel("Населённый пункт");
        JTextField suburbTextField = new JTextField("", 10);
        JLabel postcodeLabel = new JLabel("Индекс");
        JTextField postcodeTextField = new JTextField("", 10);

        JButton saveMember = new JButton("Добавить участника");
        saveMember.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                try {
                    Address newAddress = new Address(streetNoTextField.getText(),
                            streetNameTextField.getText(),
                            suburbTextField.getText(),
                            postcodeTextField.getText());
                    FamilyMember newMember = new FamilyMember(
                            nameTextField.getText(),
                            lastnameTextField.getText(),
                            (FamilyMember.Gender) genderComboBox.getSelectedItem(),
                            newAddress,
                            lifeDescriptionTextArea.getText());
                    newMember.setMaidenName(maidennameTextField.getText());
                    if (member == null) {
                        currentFamilyTree.setRoot(newMember);
                        editStatus("Добавлен корневой элемент");
                    }
                    else {
                        //add the relative
                        member.addRelative((FamilyMember.RelativeType) relativeTypeComboBox.getSelectedItem(), newMember);
                        editStatus("Добавлен новый участник");
                    }
                    displayTree(currentFamilyTree);

                }
                catch (Exception d) {
                    showErrorDialog(d);
                }
            }
        });
        JButton cancel = new JButton("Отмена");
        cancel.addActionListener(new cancelEditMemberAction(member));

        relativeTypeComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                switch ((FamilyMember.RelativeType) relativeTypeComboBox.getSelectedItem()) {
                    case ОТЕЦ:
                        genderComboBox.setSelectedItem(FamilyMember.Gender.МУЖСКОЙ);
                        maidennameTextField.setEditable(false);
                        lastnameTextField.setText(member.getLastName());
                        break;
                    case МАТЬ:
                        genderComboBox.setSelectedItem(FamilyMember.Gender.ЖЕНСКИЙ);
                        maidennameTextField.setEditable(true);
                        lastnameTextField.setText(member.getLastName());
                        break;
                    case СУПРУГ:
                        lastnameTextField.setText(member.getLastName());
                        maidennameTextField.setEditable(true);
                        break;
                    case РЕБЁНОК:
                        lastnameTextField.setText(member.getLastName());
                        maidennameTextField.setEditable(true);
                        break;
                }
            }
        });
        layout.setHorizontalGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addComponent(relativeTypeLabel)
                        .addComponent(nameLabel)
                        .addComponent(lastnameLabel)
                        .addComponent(maidennameLabel)
                        .addComponent(genderLabel)
                        .addComponent(lifeDescriptionLabel)
                        .addComponent(addressInfoLabel)
                        .addComponent(streetNoLabel)
                        .addComponent(streetNameLabel)
                        .addComponent(suburbLabel)
                        .addComponent(postcodeLabel)
                )
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addComponent(nameTextField)
                        .addComponent(relativeTypeComboBox)
                        .addComponent(lastnameTextField)
                        .addComponent(maidennameTextField)
                        .addComponent(lifeDescriptionScrollPane1)
                        .addComponent(genderComboBox)
                        .addComponent(addressInfoLabel)
                        .addComponent(streetNoTextField)
                        .addComponent(streetNameTextField)
                        .addComponent(suburbTextField)
                        .addComponent(postcodeTextField)
                )
        );

        layout.setVerticalGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(relativeTypeLabel)
                        .addComponent(relativeTypeComboBox))
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(nameLabel)
                        .addComponent(nameTextField))
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(lastnameLabel)
                        .addComponent(lastnameTextField))
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(maidennameLabel)
                        .addComponent(maidennameTextField))
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(genderLabel)
                        .addComponent(genderComboBox))
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(lifeDescriptionLabel)
                        .addComponent(lifeDescriptionScrollPane1))
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(addressInfoLabel))
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(streetNoLabel)
                        .addComponent(streetNoTextField))
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(streetNameLabel)
                        .addComponent(streetNameTextField))
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(suburbLabel)
                        .addComponent(suburbTextField))
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(postcodeLabel)
                        .addComponent(postcodeTextField))
        );

        JPanel container = new JPanel(new FlowLayout(FlowLayout.LEFT));
        container.setOpaque(false);
        container.add(saveMember);
        container.add(cancel);

        gbc.gridx = 0;
        gbc.gridy = 2;
        infoPanel.add(container, gbc);
        infoPanel.validate();
        infoPanel.repaint();
    }

    /**
     * Рекурсивный метод заполнения объекта jtree для каждого члена семьи корневого человека
     * @param top узла для заполнения
     * @param root член, из которого нужно получить данные
     */
    private void createTree(DefaultMutableTreeNode top, FamilyMember root) {
        DefaultMutableTreeNode parents = null;
        DefaultMutableTreeNode father = null;
        DefaultMutableTreeNode mother = null;
        DefaultMutableTreeNode spouse = null;
        DefaultMutableTreeNode children = null;
        DefaultMutableTreeNode child = null;
        DefaultMutableTreeNode spouseNode = null;

        if (root.has(FamilyMember.Attribute.РОДИТЕЛИ) && root == currentFamilyTree.getRoot()) {
            parents = new DefaultMutableTreeNode("Родители");
            top.add(parents);

            if (root.has(FamilyMember.Attribute.ОТЕЦ)) {
                father = new DefaultMutableTreeNode(root.getFather());
                parents.add(father);
            }

            if (root.has(FamilyMember.Attribute.МАТЬ)) {
                mother = new DefaultMutableTreeNode(root.getMother());
                parents.add(mother);
            }
        }

        if (root.has(FamilyMember.Attribute.СУПРУГ)) {
            spouseNode = new DefaultMutableTreeNode("Супруг");
            spouse = new DefaultMutableTreeNode(root.getSpouse());
            spouseNode.add(spouse);
            top.add(spouseNode);
        }

        if (root.has(FamilyMember.Attribute.ДЕТИ)) {
            children = new DefaultMutableTreeNode("Дети");
            for (FamilyMember f : root.getChildren()) {
                child = new DefaultMutableTreeNode(f);
                createTree(child, f);
                children.add(child);
            }
            top.add(children);
        }
    }

    /**
     * показывает диалоговое окно ошибки, содержащее сообщение об ошибке из исключения
     * @param e исключение для получения сообщения от
     */
    private void showErrorDialog(Exception e) {
        JOptionPane.showMessageDialog(mainFrame, "Ошибка: " + e.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
    }
}
