 /**********************************************************************
 **                                                                   **
 **               This code belongs to the KETTLE project.            **
 **                                                                   **
 ** Kettle, from version 2.2 on, is released into the public domain   **
 ** under the Lesser GNU Public License (LGPL).                       **
 **                                                                   **
 ** For more details, please read the document LICENSE.txt, included  **
 ** in this project                                                   **
 **                                                                   **
 ** http://www.kettle.be                                              **
 ** info@kettle.be                                                    **
 **                                                                   **
 **********************************************************************/

/*
 * Created on 2-jul-2003
 * 24May2006: Make DimensionLookup and CombinationLookup look more alike.
 */
package be.ibridge.kettle.trans.step.dimensionlookup;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

import be.ibridge.kettle.core.ColumnInfo;
import be.ibridge.kettle.core.Const;
import be.ibridge.kettle.core.Props;
import be.ibridge.kettle.core.Row;
import be.ibridge.kettle.core.SQLStatement;
import be.ibridge.kettle.core.database.Database;
import be.ibridge.kettle.core.database.DatabaseMeta;
import be.ibridge.kettle.core.dialog.DatabaseExplorerDialog;
import be.ibridge.kettle.core.dialog.ErrorDialog;
import be.ibridge.kettle.core.dialog.SQLEditor;
import be.ibridge.kettle.core.exception.KettleException;
import be.ibridge.kettle.core.value.Value;
import be.ibridge.kettle.core.widget.TableView;
import be.ibridge.kettle.trans.TransMeta;
import be.ibridge.kettle.trans.step.BaseStepDialog;
import be.ibridge.kettle.trans.step.BaseStepMeta;
import be.ibridge.kettle.trans.step.StepDialogInterface;
import be.ibridge.kettle.trans.step.StepMeta;
import be.ibridge.kettle.trans.step.dimensionlookup.Messages;

/**
 *  Dialog for the Dimension Lookup/Update step. 
 */
public class DimensionLookupDialog extends BaseStepDialog implements StepDialogInterface
{
	private CTabFolder   wTabFolder;
	private FormData     fdTabFolder;

	private CTabItem     wKeyTab, wFieldsTab;

	private FormData     fdKeyComp, fdFieldsComp;

	private CCombo       wConnection;

	private Label        wlTable;
	private Button       wbTable;
	private Text         wTable;

	private Label        wlCommit;
	private Text         wCommit;

	private Label        wlTk;
	private Text         wTk;

	private Label        wlTkRename;
	private Text         wTkRename;

	private Group        gTechGroup;
	
	private Label        wlAutoinc;
	private Button       wAutoinc;

	private Label        wlTableMax;
	private Button       wTableMax;	

	private Label        wlSeqButton;
	private Button       wSeqButton;			
	private Text         wSeq;    	

	private Label        wlVersion;
	private Text         wVersion;

	private Label        wlDatefield;
	private Text         wDatefield;

	private Label        wlFromdate;
	private Text         wFromdate;

	private Label        wlMinyear;
	private Text         wMinyear;	

	private Label        wlTodate;
	private Text         wTodate;

	private Label        wlMaxyear;
	private Text         wMaxyear;

	private Label        wlUpdate;
	private Button       wUpdate;

	private Label        wlKey;
	private TableView    wKey;

	private Label        wlUpIns;
	private TableView    wUpIns;

	private Button wGet, wCreate;
	private Listener lsGet, lsCreate;

	private DimensionLookupMeta input;
	private boolean backupUpdate, backupAutoInc;

	private DatabaseMeta ci;

	public DimensionLookupDialog(Shell parent, Object in, TransMeta tr, String sname)
	{
		super(parent, (BaseStepMeta)in, tr, sname);
		input=(DimensionLookupMeta)in;
	}

	public String open()
	{
		Shell parent = getParent();
		Display display = parent.getDisplay();

		shell = new Shell(parent, SWT.DIALOG_TRIM | SWT.RESIZE | SWT.MAX | SWT.MIN);
 		props.setLook(shell);

		ModifyListener lsMod = new ModifyListener()
		{
			public void modifyText(ModifyEvent e)
			{
				input.setChanged();
			}
		};
		backupChanged = input.hasChanged();
		backupUpdate = input.isUpdate();
		backupAutoInc = input.isAutoIncrement();
		ci = input.getDatabaseMeta();

		FormLayout formLayout = new FormLayout ();
		formLayout.marginWidth  = Const.FORM_MARGIN;
		formLayout.marginHeight = Const.FORM_MARGIN;

		shell.setLayout(formLayout);
		shell.setText(Messages.getString("DimensionLookupDialog.Shell.Title")); //$NON-NLS-1$

		int middle = props.getMiddlePct();
		int margin = Const.MARGIN;

		// Stepname line
		wlStepname=new Label(shell, SWT.RIGHT);
		wlStepname.setText(Messages.getString("DimensionLookupDialog.Stepname.Label")); //$NON-NLS-1$
 		props.setLook(wlStepname);
		fdlStepname=new FormData();
		fdlStepname.left = new FormAttachment(0, 0);
		fdlStepname.right= new FormAttachment(middle, -margin);
		fdlStepname.top  = new FormAttachment(0, margin);
		wlStepname.setLayoutData(fdlStepname);
		wStepname=new Text(shell, SWT.SINGLE | SWT.LEFT | SWT.BORDER);
		wStepname.setText(stepname);
 		props.setLook(wStepname);
		wStepname.addModifyListener(lsMod);
		fdStepname=new FormData();
		fdStepname.left = new FormAttachment(middle, 0);
		fdStepname.top  = new FormAttachment(0, margin);
		fdStepname.right= new FormAttachment(100, 0);
		wStepname.setLayoutData(fdStepname);


		// Connection line
		wConnection = addConnectionLine(shell, wStepname, middle, margin);
		if (input.getDatabaseMeta()==null && transMeta.nrDatabases()==1) wConnection.select(0);
		wConnection.addModifyListener(lsMod);

		wConnection.addModifyListener(new ModifyListener()
		{
			public void modifyText(ModifyEvent e)
			{
				// We have new content: change ci connection:
				ci = transMeta.findDatabase(wConnection.getText());
				setFlags();
			}
		});

		// Table line...
		wlTable=new Label(shell, SWT.RIGHT);
		wlTable.setText(Messages.getString("DimensionLookupDialog.TargeTable.Label")); //$NON-NLS-1$
 		props.setLook(wlTable);
		FormData fdlTable=new FormData();
		fdlTable.left = new FormAttachment(0, 0);
		fdlTable.right= new FormAttachment(middle, -margin);
		fdlTable.top  = new FormAttachment(wConnection, margin);
		wlTable.setLayoutData(fdlTable);

		wbTable=new Button(shell, SWT.PUSH| SWT.CENTER);
 		props.setLook(wbTable);
		wbTable.setText(Messages.getString("DimensionLookupDialog.Browse.Button")); //$NON-NLS-1$
		FormData fdbTable=new FormData();
		fdbTable.right= new FormAttachment(100, 0);
		fdbTable.top  = new FormAttachment(wConnection, margin);
		wbTable.setLayoutData(fdbTable);

		wTable=new Text(shell, SWT.SINGLE | SWT.LEFT | SWT.BORDER);
 		props.setLook(wTable);
		wTable.addModifyListener(lsMod);
		FormData fdTable=new FormData();
		fdTable.left = new FormAttachment(middle, 0);
		fdTable.top  = new FormAttachment(wConnection, margin);
		fdTable.right= new FormAttachment(wbTable, 0);
		wTable.setLayoutData(fdTable);

		// Commit size ...
		wlCommit=new Label(shell, SWT.RIGHT);
		wlCommit.setText(Messages.getString("DimensionLookupDialog.Commit.Label")); //$NON-NLS-1$
 		props.setLook(wlCommit);
		FormData fdlCommit=new FormData();
		fdlCommit.left = new FormAttachment(0, 0);
		fdlCommit.right= new FormAttachment(middle, -margin);
		fdlCommit.top  = new FormAttachment(wTable, margin);
		wlCommit.setLayoutData(fdlCommit);
		wCommit=new Text(shell, SWT.SINGLE | SWT.LEFT | SWT.BORDER);
 		props.setLook(wCommit);
		wCommit.addModifyListener(lsMod);
		FormData fdCommit=new FormData();
		fdCommit.left = new FormAttachment(middle, 0);
		fdCommit.top  = new FormAttachment(wTable, margin);
		fdCommit.right= new FormAttachment(100, 0);
		wCommit.setLayoutData(fdCommit);
		
		wlTkRename=new Label(shell, SWT.RIGHT);
	
		wTabFolder = new CTabFolder(shell, SWT.BORDER);
 		props.setLook(wTabFolder, Props.WIDGET_STYLE_TAB);
 		
		//////////////////////////
		// START OF KEY TAB    ///
		///
		wKeyTab=new CTabItem(wTabFolder, SWT.NONE);
		wKeyTab.setText(Messages.getString("DimensionLookupDialog.KeyTab.CTabItem")); //$NON-NLS-1$

		FormLayout keyLayout = new FormLayout ();
		keyLayout.marginWidth  = 3;
		keyLayout.marginHeight = 3;

		Composite wKeyComp = new Composite(wTabFolder, SWT.NONE);
 		props.setLook(wKeyComp);
		wKeyComp.setLayout(keyLayout);

		//
		// The Lookup fields: usually the key
		//
		wlKey=new Label(wKeyComp, SWT.NONE);
		wlKey.setText(Messages.getString("DimensionLookupDialog.KeyFields.Label")); //$NON-NLS-1$
 		props.setLook(wlKey);
		FormData fdlKey=new FormData();
		fdlKey.left  = new FormAttachment(0, 0);
		fdlKey.top   = new FormAttachment(0, margin);
		fdlKey.right = new FormAttachment(100, 0);
		wlKey.setLayoutData(fdlKey);

		int nrKeyCols=2;
		int nrKeyRows=(input.getKeyStream()!=null?input.getKeyStream().length:1);

		ColumnInfo[] ciKey=new ColumnInfo[nrKeyCols];
		ciKey[0]=new ColumnInfo(Messages.getString("DimensionLookupDialog.ColumnInfo.DimensionField"),  ColumnInfo.COLUMN_TYPE_TEXT,   false); //$NON-NLS-1$
		ciKey[1]=new ColumnInfo(Messages.getString("DimensionLookupDialog.ColumnInfo.FieldInStream"),  ColumnInfo.COLUMN_TYPE_TEXT,   false); //$NON-NLS-1$

		wKey=new TableView(wKeyComp,
						      SWT.BORDER | SWT.FULL_SELECTION | SWT.MULTI | SWT.V_SCROLL | SWT.H_SCROLL,
						      ciKey,
						      nrKeyRows,
						      lsMod,
							  props
						      );

		FormData fdKey=new FormData();
		fdKey.left  = new FormAttachment(0, 0);
		fdKey.top   = new FormAttachment(wlKey, margin);
		fdKey.right = new FormAttachment(100, 0);
		fdKey.bottom= new FormAttachment(100, 0);
		wKey.setLayoutData(fdKey);

		fdKeyComp = new FormData();
		fdKeyComp.left  = new FormAttachment(0, 0);
		fdKeyComp.top   = new FormAttachment(0, 0);
		fdKeyComp.right = new FormAttachment(100, 0);
		fdKeyComp.bottom= new FormAttachment(100, 0);
		wKeyComp.setLayoutData(fdKeyComp);

		wKeyComp.layout();
		wKeyTab.setControl(wKeyComp);

		/////////////////////////////////////////////////////////////
		/// END OF KEY TAB
		/////////////////////////////////////////////////////////////

		// Fields tab...
		//
		wFieldsTab = new CTabItem(wTabFolder, SWT.NONE);
		wFieldsTab.setText(Messages.getString("DimensionLookupDialog.FieldsTab.CTabItem.Title")); //$NON-NLS-1$

		FormLayout fieldsLayout = new FormLayout ();
		fieldsLayout.marginWidth  = Const.FORM_MARGIN;
		fieldsLayout.marginHeight = Const.FORM_MARGIN;

		Composite wFieldsComp = new Composite(wTabFolder, SWT.NONE);
		wFieldsComp.setLayout(fieldsLayout);
 		props.setLook(wFieldsComp);

		// THE UPDATE/INSERT TABLE
		wlUpIns=new Label(wFieldsComp, SWT.NONE);
		wlUpIns.setText(Messages.getString("DimensionLookupDialog.UpdateOrInsertFields.Label")); //$NON-NLS-1$
 		props.setLook(wlUpIns);
		FormData fdlUpIns=new FormData();
		fdlUpIns.left  = new FormAttachment(0, 0);
		fdlUpIns.top   = new FormAttachment(0, margin);
		wlUpIns.setLayoutData(fdlUpIns);

		int UpInsCols=3;
		int UpInsRows= (input.getFieldStream()!=null?input.getFieldStream().length:1);

		final ColumnInfo[] ciUpIns=new ColumnInfo[UpInsCols];
		ciUpIns[0]=new ColumnInfo(Messages.getString("DimensionLookupDialog.ColumnInfo.DimensionField"),              ColumnInfo.COLUMN_TYPE_TEXT,   false); //$NON-NLS-1$
		ciUpIns[1]=new ColumnInfo(Messages.getString("DimensionLookupDialog.ColumnInfo.StreamField"),                 ColumnInfo.COLUMN_TYPE_TEXT,   false); //$NON-NLS-1$
		ciUpIns[2]=new ColumnInfo(Messages.getString("DimensionLookupDialog.ColumnInfo.TypeOfDimensionUpdate"),     ColumnInfo.COLUMN_TYPE_CCOMBO, input.isUpdate()?DimensionLookupMeta.typeDesc:DimensionLookupMeta.typeDescLookup ); //$NON-NLS-1$

		wUpIns=new TableView(wFieldsComp,
							  SWT.BORDER | SWT.FULL_SELECTION | SWT.MULTI | SWT.V_SCROLL | SWT.H_SCROLL,
							  ciUpIns,
							  UpInsRows,
							  lsMod,
							  props
							  );

		FormData fdUpIns=new FormData();
		fdUpIns.left  = new FormAttachment(0, 0);
		fdUpIns.top   = new FormAttachment(wlUpIns, margin);
		fdUpIns.right = new FormAttachment(100, 0);
		fdUpIns.bottom= new FormAttachment(100, 100);
		wUpIns.setLayoutData(fdUpIns);

		fdFieldsComp=new FormData();
		fdFieldsComp.left  = new FormAttachment(0, 0);
		fdFieldsComp.top   = new FormAttachment(0, 0);
		fdFieldsComp.right = new FormAttachment(100, 0);
		fdFieldsComp.bottom= new FormAttachment(100, 0);
		wFieldsComp.setLayoutData(fdFieldsComp);

		wFieldsComp.layout();
		wFieldsTab.setControl(wFieldsComp);

		fdTabFolder = new FormData();
		fdTabFolder.left  = new FormAttachment(0, 0);
		fdTabFolder.top   = new FormAttachment(wCommit, margin);
		fdTabFolder.right = new FormAttachment(100, 0);
		fdTabFolder.bottom= new FormAttachment(wlTkRename, -2 * margin);		
		wTabFolder.setLayoutData(fdTabFolder);

		
        ////////////////////////////////////////////////////////////////////
		// The next parts are from the bottom upwards so that the table
		// gets a chance to expand when the dialog is enlarged.

		// THE BOTTOM BUTTONS 
		wOK=new Button(shell, SWT.PUSH);
		wOK.setText(Messages.getString("System.Button.OK")); //$NON-NLS-1$		
		wGet=new Button(shell, SWT.PUSH);
		wGet.setText(Messages.getString("DimensionLookupDialog.GetFields.Button")); //$NON-NLS-1$
		wCreate=new Button(shell, SWT.PUSH);
		wCreate.setText(Messages.getString("DimensionLookupDialog.SQL.Button")); //$NON-NLS-1$
		wCancel=new Button(shell, SWT.PUSH);
		wCancel.setText(Messages.getString("System.Button.Cancel")); //$NON-NLS-1$

		setButtonPositions(new Button[] { wOK, wGet, wCreate, wCancel }, margin, null);

		// Add listeners
		lsOK       = new Listener() { public void handleEvent(Event e) { ok();     } };
		lsGet      = new Listener() { public void handleEvent(Event e) { get();    } };
		lsCreate   = new Listener() { public void handleEvent(Event e) { create(); } };
		lsCancel   = new Listener() { public void handleEvent(Event e) { cancel(); } };

		wOK.addListener    (SWT.Selection, lsOK    );
		wGet.addListener   (SWT.Selection, lsGet   );
		wCreate.addListener(SWT.Selection, lsCreate);
		wCancel.addListener(SWT.Selection, lsCancel);

		// Update the dimension?
		wlUpdate=new Label(shell, SWT.RIGHT);
		wlUpdate.setText(Messages.getString("DimensionLookupDialog.Update.Label")); //$NON-NLS-1$
 		props.setLook(wlUpdate);
		FormData fdlUpdate=new FormData();
		fdlUpdate.left   = new FormAttachment(0, 0);
		fdlUpdate.right  = new FormAttachment(middle, -margin);
		fdlUpdate.bottom = new FormAttachment(wOK, -2 * margin);
		wlUpdate.setLayoutData(fdlUpdate);
		wUpdate=new Button(shell, SWT.CHECK);
 		props.setLook(wUpdate);
		FormData fdUpdate=new FormData();
		fdUpdate.left = new FormAttachment(middle, 0);
		fdUpdate.bottom = new FormAttachment(wOK, -2 * margin);
		wUpdate.setLayoutData(fdUpdate);

		// Clicking on update changes the options in the update combo boxes!
		wUpdate.addSelectionListener(new SelectionAdapter()
			{
				public void widgetSelected(SelectionEvent e)
				{
					input.setUpdate(!input.isUpdate());
					input.setChanged();

					setFlags();
				}
			}
		);

		// Todate line
		wlTodate=new Label(shell, SWT.RIGHT);
		wlTodate.setText(Messages.getString("DimensionLookupDialog.Todate.Label")); //$NON-NLS-1$
 		props.setLook(wlTodate);
		FormData fdlTodate=new FormData();
		fdlTodate.left  = new FormAttachment(0, 0);
		fdlTodate.right = new FormAttachment(middle, -margin);
		fdlTodate.bottom= new FormAttachment(wUpdate, -margin);
		wlTodate.setLayoutData(fdlTodate); 
		wTodate=new Text(shell, SWT.SINGLE | SWT.LEFT | SWT.BORDER);
 		props.setLook(wTodate);
		wTodate.addModifyListener(lsMod);
		FormData fdTodate=new FormData();
		fdTodate.left   = new FormAttachment(middle, 0);
		fdTodate.right  = new FormAttachment(middle+(100-middle)/3, -margin);
		fdTodate.bottom = new FormAttachment(wUpdate, -margin); 
		wTodate.setLayoutData(fdTodate); 
		
		// Maxyear line
		wlMaxyear=new Label(shell, SWT.RIGHT);
		wlMaxyear.setText(Messages.getString("DimensionLookupDialog.Maxyear.Label")); //$NON-NLS-1$
 		props.setLook(wlMaxyear);
		FormData fdlMaxyear=new FormData();
		fdlMaxyear.left  = new FormAttachment(wTodate, margin);
		fdlMaxyear.right = new FormAttachment(middle+2*(100-middle)/3, -margin);
		fdlMaxyear.bottom = new FormAttachment(wUpdate, -margin);
		wlMaxyear.setLayoutData(fdlMaxyear); 
		wMaxyear=new Text(shell, SWT.SINGLE | SWT.LEFT | SWT.BORDER);
 		props.setLook(wMaxyear);
		wMaxyear.addModifyListener(lsMod);
		FormData fdMaxyear=new FormData();
		fdMaxyear.left = new FormAttachment(wlMaxyear, margin);
		fdMaxyear.right= new FormAttachment(100, 0);
		fdMaxyear.bottom  = new FormAttachment(wUpdate, -margin);
		wMaxyear.setLayoutData(fdMaxyear);
		wMaxyear.setToolTipText(Messages.getString("DimensionLookupDialog.Maxyear.ToolTip")); //$NON-NLS-1$
		
		// Fromdate line
		//
		//  0 [wlFromdate] middle [wFromdate] (100-middle)/3 [wlMinyear] 2*(100-middle)/3 [wMinyear] 100%
		//
		wlFromdate=new Label(shell, SWT.RIGHT);
		wlFromdate.setText(Messages.getString("DimensionLookupDialog.Fromdate.Label")); //$NON-NLS-1$
 		props.setLook(wlFromdate);
		FormData fdlFromdate=new FormData();
		fdlFromdate.left = new FormAttachment(0, 0);
		fdlFromdate.right= new FormAttachment(middle, -margin);
		fdlFromdate.bottom  = new FormAttachment(wTodate, -margin);
		wlFromdate.setLayoutData(fdlFromdate);
		wFromdate=new Text(shell, SWT.SINGLE | SWT.LEFT | SWT.BORDER);
 		props.setLook(wFromdate);
		wFromdate.addModifyListener(lsMod);
		FormData fdFromdate=new FormData();
		fdFromdate.left = new FormAttachment(middle, 0);
		fdFromdate.right= new FormAttachment(middle+(100-middle)/3, -margin);
		fdFromdate.bottom  = new FormAttachment(wTodate, -margin);
		wFromdate.setLayoutData(fdFromdate);

		// Minyear line
		wlMinyear=new Label(shell, SWT.RIGHT);
		wlMinyear.setText(Messages.getString("DimensionLookupDialog.Minyear.Label")); //$NON-NLS-1$
 		props.setLook(wlMinyear); 		
		FormData fdlMinyear=new FormData();
		fdlMinyear.left  = new FormAttachment(wFromdate, margin);
		fdlMinyear.right = new FormAttachment(middle+2*(100-middle)/3, -margin);
		fdlMinyear.bottom = new FormAttachment(wTodate, -margin);
		wlMinyear.setLayoutData(fdlMinyear);
		wMinyear=new Text(shell, SWT.SINGLE | SWT.LEFT | SWT.BORDER);
 		props.setLook(wMinyear);
		wMinyear.addModifyListener(lsMod);
		FormData fdMinyear=new FormData();
		fdMinyear.left = new FormAttachment(wlMinyear, margin);
		fdMinyear.right= new FormAttachment(100, 0);
		fdMinyear.bottom = new FormAttachment(wTodate, -margin);
		wMinyear.setLayoutData(fdMinyear);
		wMinyear.setToolTipText(Messages.getString("DimensionLookupDialog.Minyear.ToolTip")); //$NON-NLS-1$

		// Datefield line
		wlDatefield=new Label(shell, SWT.RIGHT);
		wlDatefield.setText(Messages.getString("DimensionLookupDialog.Datefield.Label")); //$NON-NLS-1$
 		props.setLook(wlDatefield);
		FormData fdlDatefield=new FormData();
		fdlDatefield.left = new FormAttachment(0, 0);
		fdlDatefield.right= new FormAttachment(middle, -margin);
		fdlDatefield.bottom = new FormAttachment(wMinyear, -margin);
		wlDatefield.setLayoutData(fdlDatefield);
		wDatefield=new Text(shell, SWT.SINGLE | SWT.LEFT | SWT.BORDER);
 		props.setLook(wDatefield);
		wDatefield.addModifyListener(lsMod);
		FormData fdDatefield=new FormData();
		fdDatefield.left   = new FormAttachment(middle, 0);
		fdDatefield.bottom = new FormAttachment(wMinyear, -margin);
		fdDatefield.right  = new FormAttachment(100, 0);
		wDatefield.setLayoutData(fdDatefield);

		// Version key field:
		wlVersion=new Label(shell, SWT.RIGHT);
		wlVersion.setText(Messages.getString("DimensionLookupDialog.Version.Label")); //$NON-NLS-1$
 		props.setLook(wlVersion);
		FormData fdlVersion=new FormData();
		fdlVersion.left    = new FormAttachment(0, 0);
		fdlVersion.right   = new FormAttachment(middle, -margin);
		fdlVersion.bottom  = new FormAttachment(wDatefield, -margin);
		wlVersion.setLayoutData(fdlVersion);
		wVersion=new Text(shell, SWT.SINGLE | SWT.LEFT | SWT.BORDER);
 		props.setLook(wVersion);
		wVersion.addModifyListener(lsMod);
		FormData fdVersion=new FormData();
		fdVersion.left   = new FormAttachment(middle, 0);
		fdVersion.bottom = new FormAttachment(wDatefield, -margin);
		fdVersion.right  = new FormAttachment(100, 0);
		wVersion.setLayoutData(fdVersion);

		////////////////////////////////////////////////////
		// The key creation box
		////////////////////////////////////////////////////
		gTechGroup = new Group(shell, SWT.SHADOW_ETCHED_IN);
		gTechGroup.setText(Messages.getString("DimensionLookupDialog.TechGroup.Label")); //$NON-NLS-1$;
		GridLayout gridLayout = new GridLayout(3, false);
		gTechGroup.setLayout(gridLayout);
		FormData fdTechGroup=new FormData();
		fdTechGroup.left   = new FormAttachment(middle, 0);
		fdTechGroup.bottom = new FormAttachment(wVersion, -margin);	
		fdTechGroup.right  = new FormAttachment(100, 0);
		gTechGroup.setBackground(shell.getBackground()); // the default looks ugly
		gTechGroup.setLayoutData(fdTechGroup);

		// Use maximum of table + 1
		wTableMax=new Button(gTechGroup, SWT.RADIO);
 		props.setLook(wTableMax);
 		wTableMax.setSelection(false);
		GridData gdTableMax=new GridData();
		wTableMax.setLayoutData(gdTableMax);
		wTableMax.setToolTipText(Messages.getString("DimensionLookupDialog.TableMaximum.Tooltip",Const.CR)); //$NON-NLS-1$ //$NON-NLS-2$
		wlTableMax=new Label(gTechGroup, SWT.LEFT);
		wlTableMax.setText(Messages.getString("DimensionLookupDialog.TableMaximum.Label")); //$NON-NLS-1$
 		props.setLook(wlTableMax);
		GridData gdlTableMax = new GridData(GridData.FILL_BOTH);
		gdlTableMax.horizontalSpan = 2; gdlTableMax.verticalSpan = 1;
		wlTableMax.setLayoutData(gdlTableMax);
		
		// Sequence Check Button
		wSeqButton=new Button(gTechGroup, SWT.RADIO);
 		props.setLook(wSeqButton);
 		wSeqButton.setSelection(false);
 		GridData gdSeqButton=new GridData();
		wSeqButton.setLayoutData(gdSeqButton);
		wSeqButton.setToolTipText(Messages.getString("DimensionLookupDialog.Sequence.Tooltip",Const.CR)); //$NON-NLS-1$ //$NON-NLS-2$		
		wlSeqButton=new Label(gTechGroup, SWT.LEFT);
		wlSeqButton.setText(Messages.getString("DimensionLookupDialog.Sequence.Label")); //$NON-NLS-1$
 		props.setLook(wlSeqButton); 	
 		GridData gdlSeqButton=new GridData();
		wlSeqButton.setLayoutData(gdlSeqButton);

		wSeq=new Text(gTechGroup, SWT.SINGLE | SWT.LEFT | SWT.BORDER);
 		props.setLook(wSeq);
		wSeq.addModifyListener(lsMod);
		GridData gdSeq=new GridData(GridData.FILL_HORIZONTAL);
		wSeq.setLayoutData(gdSeq);
		wSeq.addFocusListener(new FocusListener() {
			public void focusGained(FocusEvent arg0) {
				input.setTechKeyCreation(DimensionLookupMeta.CREATION_METHOD_SEQUENCE);
				wSeqButton.setSelection(true);
				wAutoinc.setSelection(false);
				wTableMax.setSelection(false);				
			}

			public void focusLost(FocusEvent arg0) {
			} 
		});		
		
		// Use an autoincrement field?
		wAutoinc=new Button(gTechGroup, SWT.RADIO);
 		props.setLook(wAutoinc);
 		wAutoinc.setSelection(false);
 		GridData gdAutoinc=new GridData();
		wAutoinc.setLayoutData(gdAutoinc);
		wAutoinc.setToolTipText(Messages.getString("DimensionLookupDialog.AutoincButton.Tooltip",Const.CR)); //$NON-NLS-1$ //$NON-NLS-2$
		wlAutoinc=new Label(gTechGroup, SWT.LEFT);
		wlAutoinc.setText(Messages.getString("DimensionLookupDialog.Autoincrement.Label")); //$NON-NLS-1$
 		props.setLook(wlAutoinc);
 		GridData gdlAutoinc=new GridData();
		wlAutoinc.setLayoutData(gdlAutoinc);

		setTableMax(); 
		setSequence();
		setAutoincUse();		
		
		// Technical key field:
		wlTk=new Label(shell, SWT.RIGHT);
		wlTk.setText(Messages.getString("DimensionLookupDialog.TechnicalKeyField.Label")); //$NON-NLS-1$
 		props.setLook(wlTk);
		FormData fdlTk=new FormData();
		fdlTk.left = new FormAttachment(0, 0);
		fdlTk.right= new FormAttachment(middle, -margin);
		fdlTk.bottom  = new FormAttachment(gTechGroup, -margin);
		
		wlTk.setLayoutData(fdlTk);
		wTk=new Text(shell, SWT.SINGLE | SWT.LEFT | SWT.BORDER);
 		props.setLook(wTk);
		wTk.addModifyListener(lsMod);
		FormData fdTk=new FormData();
		fdTk.left = new FormAttachment(middle, 0);
		//fdTk.top  = new FormAttachment(wTabFolder, margin);
		fdTk.bottom  = new FormAttachment(gTechGroup, -margin);		
		fdTk.right= new FormAttachment(50+middle/2, 0);
		wTk.setLayoutData(fdTk);

		wlTkRename.setText(Messages.getString("DimensionLookupDialog.NewName.Label")); //$NON-NLS-1$
 		props.setLook(wlTkRename);
		FormData fdlTkRename=new FormData();
		fdlTkRename.left = new FormAttachment(50+middle/2, margin);
		fdlTkRename.bottom = new FormAttachment(gTechGroup, -margin);
		wlTkRename.setLayoutData(fdlTkRename);
		wTkRename=new Text(shell, SWT.SINGLE | SWT.LEFT | SWT.BORDER);
 		props.setLook(wTkRename);
		wTkRename.addModifyListener(lsMod);
		FormData fdTkRename=new FormData();
		fdTkRename.left = new FormAttachment(wlTkRename, margin);
		fdTkRename.bottom  = new FormAttachment(gTechGroup, -margin);
		fdTkRename.right= new FormAttachment(100, 0);
		wTkRename.setLayoutData(fdTkRename);

		lsDef=new SelectionAdapter() { public void widgetDefaultSelected(SelectionEvent e) { ok(); } };

		wStepname.addSelectionListener( lsDef );
		wTable.addSelectionListener( lsDef );
		wCommit.addSelectionListener( lsDef );
		wTk.addSelectionListener( lsDef );
		wTkRename.addSelectionListener( lsDef );
		wSeq.addSelectionListener( lsDef );
		wVersion.addSelectionListener( lsDef );
		wDatefield.addSelectionListener( lsDef );
		wFromdate.addSelectionListener( lsDef );
		wMinyear.addSelectionListener( lsDef );
		wTodate.addSelectionListener( lsDef );
		wMaxyear.addSelectionListener( lsDef );

		// Detect X or ALT-F4 or something that kills this window...
		shell.addShellListener(	new ShellAdapter() { public void shellClosed(ShellEvent e) { cancel(); } } );

		wbTable.addSelectionListener
		(
			new SelectionAdapter()
			{
				public void widgetSelected(SelectionEvent e)
				{
					getTableName();
				}
			}
		);

		wTabFolder.setSelection(0);

		// Set the shell size, based upon previous time...
		setSize();

		getData();
		input.setChanged(backupChanged);

		shell.open();
		while (!shell.isDisposed())
		{
				if (!display.readAndDispatch()) display.sleep();
		}
		return stepname;
	}

	public void setFlags()
	{
		ColumnInfo colinf =new ColumnInfo(Messages.getString("DimensionLookupDialog.ColumnInfo.Type"),      ColumnInfo.COLUMN_TYPE_CCOMBO,  //$NON-NLS-1$
			  input.isUpdate()?
				 DimensionLookupMeta.typeDesc:
				 DimensionLookupMeta.typeDescLookup
		);
		wUpIns.setColumnInfo(2, colinf);

		if (input.isUpdate())
		{
			wUpIns.setColumnText(2, Messages.getString("DimensionLookupDialog.UpdateOrInsertFields.ColumnText.SteamFieldToCompare")); //$NON-NLS-1$
			wUpIns.setColumnText(3, Messages.getString("DimensionLookupDialog.UpdateOrInsertFields.ColumnTextTypeOfDimensionUpdate")); //$NON-NLS-1$
			wUpIns.setColumnToolTip(2, Messages.getString("DimensionLookupDialog.UpdateOrInsertFields.ColumnToolTip")+Const.CR+"Punch Through: Kimball Type I"+Const.CR+"Update: Correct error in last version"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		}
		else
		{
			wUpIns.setColumnText(2, Messages.getString("DimensionLookupDialog.UpdateOrInsertFields.ColumnText.NewNameOfOutputField")); //$NON-NLS-1$
			wUpIns.setColumnText(3, Messages.getString("DimensionLookupDialog.UpdateOrInsertFields.ColumnText.TypeOfReturnField")); //$NON-NLS-1$
			wUpIns.setColumnToolTip(2, Messages.getString("DimensionLookupDialog.UpdateOrInsertFields.ColumnToolTip2")); //$NON-NLS-1$
		}
		wUpIns.optWidth(true);

        // In case of lookup: disable commitsize, etc.
        wlCommit.setEnabled( wUpdate.getSelection() );
        wCommit.setEnabled( wUpdate.getSelection() );
        wlAutoinc.setEnabled( wUpdate.getSelection() );
        wAutoinc.setEnabled( wUpdate.getSelection() );
        wSeq.setEnabled( wUpdate.getSelection() );
        wlMinyear.setEnabled( wUpdate.getSelection() );
        wMinyear.setEnabled( wUpdate.getSelection() );
        wlMaxyear.setEnabled( wUpdate.getSelection() );
        wMaxyear.setEnabled( wUpdate.getSelection() );
        wlMinyear.setEnabled( wUpdate.getSelection() );
        wMinyear.setEnabled( wUpdate.getSelection() );
        
		setAutoincUse();
		setSequence();
		setTableMax();
	}
    
	public void setAutoincUse()
	{
		boolean enable = (ci == null) || ci.supportsAutoinc();
		wlAutoinc.setEnabled(enable);
		wAutoinc.setEnabled(enable);
		if ( enable == false && 
			 wAutoinc.getSelection() == true )
		{
			wAutoinc.setSelection(false);
			wSeqButton.setSelection(false);
			wTableMax.setSelection(true);
		}		
	}

	public void setTableMax()
	{
		wlTableMax.setEnabled(true);
		wTableMax.setEnabled(true);
	}
	
	public void setSequence()
	{
		boolean seq = (ci == null) || ci.supportsSequences();
		wSeq.setEnabled(seq);
		wlSeqButton.setEnabled(seq);
		wSeqButton.setEnabled(seq);
		if ( seq == false && 
			 wSeqButton.getSelection() == true ) 
		{
		    wAutoinc.setSelection(false);
			wSeqButton.setSelection(false);
			wTableMax.setSelection(true);
		}		
	}    	
	
	/**
	 * Copy information from the meta-data input to the dialog fields.
	 */
	public void getData()
	{
		int i;
		log.logDebug(toString(), Messages.getString("DimensionLookupDialog.Log.GettingKeyInfo")); //$NON-NLS-1$

		if (input.getKeyStream()!=null)
		for (i=0;i<input.getKeyStream().length;i++)
		{
			TableItem item = wKey.table.getItem(i);
			if (input.getKeyLookup()[i]!=null) item.setText(1, input.getKeyLookup()[i]);
			if (input.getKeyStream()[i]!=null) item.setText(2, input.getKeyStream()[i]);
		}

		if (input.getFieldStream()!=null)
		for (i=0;i<input.getFieldStream().length;i++)
		{
			TableItem item = wUpIns.table.getItem(i);
			if (input.getFieldLookup()[i]!=null) item.setText(1, input.getFieldLookup()[i]);
			if (input.getFieldStream()[i]!=null) item.setText(2, input.getFieldStream()[i]);
			item.setText(3, DimensionLookupMeta.getUpdateType(input.isUpdate(), input.getFieldUpdate()[i]) );
		}

		wUpdate.setSelection( input.isUpdate() );

		if (input.getTableName()!=null)       wTable.setText( input.getTableName() );
		if (input.getKeyField()!=null)        wTk.setText(input.getKeyField());
		if (input.getKeyRename()!=null)       wTkRename.setText(input.getKeyRename());

		wAutoinc.setSelection( input.isAutoIncrement() );

		if (input.getVersionField()!=null)    wVersion.setText(input.getVersionField());
		if (input.getSequenceName()!=null)        wSeq.setText(input.getSequenceName());
		if (input.getDatabaseMeta()!=null)   wConnection.setText(input.getDatabaseMeta().getName());
		else if (transMeta.nrDatabases()==1)
		{
			wConnection.setText( transMeta.getDatabase(0).getName() );
		}
		if (input.getDateField()!=null)    wDatefield.setText(input.getDateField());
		if (input.getDateFrom()!=null)     wFromdate.setText(input.getDateFrom());
		if (input.getDateTo()!=null)       wTodate.setText(input.getDateTo());

		String techKeyCreation = input.getTechKeyCreation(); 
		if ( techKeyCreation == null )  {		    
		    // Determine the creation of the technical key for
			// backwards compatibility. Can probably be removed at
			// version 3.x or so (Sven Boden).
		    DatabaseMeta database = input.getDatabaseMeta(); 
		    if ( database == null || ! database.supportsAutoinc() )  
		    {
 			    input.setAutoIncrement(false);			
		    }		
		    wAutoinc.setSelection(input.isAutoIncrement());
		    
		    wSeqButton.setSelection(input.getSequenceName() != null && input.getSequenceName().length() > 0);
		    if ( input.isAutoIncrement() == false && 
			     (input.getSequenceName() == null || input.getSequenceName().length() <= 0) ) 
		    {
 			    wTableMax.setSelection(true); 			    
		    }
		    
			if ( database != null && database.supportsSequences() && 
				 input.getSequenceName() != null) 
			{
				wSeq.setText(input.getSequenceName());
				input.setAutoIncrement(false);
				wTableMax.setSelection(false);
			}
		}
		else
		{
		    // KETTLE post 2.2 version:
			// The "creation" field now determines the behaviour of the
			// key creation.
			if ( DimensionLookupMeta.CREATION_METHOD_AUTOINC.equals(techKeyCreation))  
			{
			    wAutoinc.setSelection(true);
			}
			else if ( ( DimensionLookupMeta.CREATION_METHOD_SEQUENCE.equals(techKeyCreation)) )
			{
				wSeqButton.setSelection(true);
			}
			else // the rest
			{
				wTableMax.setSelection(true);
				input.setTechKeyCreation(DimensionLookupMeta.CREATION_METHOD_TABLEMAX);
			}
			if ( input.getSequenceName() != null )
			{
    	        wSeq.setText(input.getSequenceName());
			}
		}
		
		
		wCommit.setText(""+input.getCommitSize()); //$NON-NLS-1$

		wMinyear.setText(""+input.getMinYear()); //$NON-NLS-1$
		wMaxyear.setText(""+input.getMaxYear()); //$NON-NLS-1$

		wUpIns.removeEmptyRows();
		wUpIns.setRowNums();
		wUpIns.optWidth(true);
		wKey.removeEmptyRows();
		wKey.setRowNums();
		wKey.optWidth(true);

		ci = transMeta.findDatabase(wConnection.getText());

        setFlags();

		wStepname.selectAll();
	}

	private void cancel()
	{
		stepname=null;
		input.setChanged(backupChanged);
		input.setUpdate( backupUpdate );
		input.setAutoIncrement( backupAutoInc );
		dispose();
	}

	private void ok()
	{
		getInfo(input);

		stepname = wStepname.getText(); // return value

		if (input.getDatabaseMeta()==null)
		{
			MessageBox mb = new MessageBox(shell, SWT.OK | SWT.ICON_ERROR );
			mb.setMessage(Messages.getString("DimensionLookupDialog.InvalidConnection.DialogMessage")); //$NON-NLS-1$
			mb.setText(Messages.getString("DimensionLookupDialog.InvalidConnection.DialogTitle")); //$NON-NLS-1$
			mb.open();
		}

		dispose();
	}

	private void getInfo(DimensionLookupMeta in)
	{
		//Table ktable = wKey.table;
		int nrkeys = wKey.nrNonEmpty();
		int nrfields = wUpIns.nrNonEmpty();

		in.allocate(nrkeys, nrfields);

		log.logDebug(toString(), Messages.getString("DimensionLookupDialog.Log.FoundKeys",String.valueOf(nrkeys))); //$NON-NLS-1$ //$NON-NLS-2$
		for (int i=0;i<nrkeys;i++)
		{
			TableItem item = wKey.getNonEmpty(i);
			in.getKeyLookup()[i] = item.getText(1);
			in.getKeyStream()[i] = item.getText(2);
		}

		log.logDebug(toString(), Messages.getString("DimensionLookupDialog.Log.FoundFields",String.valueOf(nrfields))); //$NON-NLS-1$ //$NON-NLS-2$
		for (int i=0;i<nrfields;i++)
		{
			TableItem item        = wUpIns.getNonEmpty(i);
			in.getFieldLookup()[i]  = item.getText(1);
			in.getFieldStream()[i]  = item.getText(2);
			in.getFieldUpdate()[i]  = DimensionLookupMeta.getUpdateType(in.isUpdate(), item.getText(3));
		}

		in.setTableName( wTable.getText() );
		in.setKeyField( wTk.getText() );
		in.setKeyRename( wTkRename.getText() );
		if ( wAutoinc.getSelection() == true )  
		{
			in.setTechKeyCreation(DimensionLookupMeta.CREATION_METHOD_AUTOINC);
			in.setAutoIncrement( true );   // for downwards compatibility
			in.setSequenceName( null );
		}
		else if ( wSeqButton.getSelection() == true )
		{
			in.setTechKeyCreation(DimensionLookupMeta.CREATION_METHOD_SEQUENCE);
			in.setAutoIncrement(false);
			in.setSequenceName( wSeq.getText() );
		}
		else  // all the rest
		{
			in.setTechKeyCreation(DimensionLookupMeta.CREATION_METHOD_TABLEMAX);
			in.setAutoIncrement( false );
			in.setSequenceName( null );
		}

		in.setAutoIncrement( wAutoinc.getSelection() );

		if (in.getKeyRename()!=null && in.getKeyRename().equalsIgnoreCase(in.getKeyField()))
			in.setKeyRename( null ); // Don't waste space&time if it's the same

		in.setVersionField( wVersion.getText() );
		in.setDatabaseMeta( transMeta.findDatabase(wConnection.getText()) );
		in.setDateField( wDatefield.getText() );
		in.setDateFrom( wFromdate.getText() );
		in.setDateTo( wTodate.getText() );

		in.setUpdate( wUpdate.getSelection() );

		in.setCommitSize( Const.toInt(wCommit.getText(), 0) );
		in.setMinYear( Const.toInt(wMinyear.getText(), Const.MIN_YEAR) );
		in.setMaxYear( Const.toInt(wMaxyear.getText(), Const.MAX_YEAR) );
	}

	private void getTableName()
	{
		int connr = wConnection.getSelectionIndex();
		DatabaseMeta inf = transMeta.getDatabase(connr);

		log.logDebug(toString(), Messages.getString("DimensionLookupDialog.Log.LookingAtConnection")+inf.toString()); //$NON-NLS-1$

		DatabaseExplorerDialog std = new DatabaseExplorerDialog(shell, props, SWT.NONE, inf, transMeta.getDatabases());
		std.setSelectedTable(wTable.getText());
		String tableName = (String)std.open();
		if (tableName != null)
		{
			wTable.setText(tableName);
		}
	}

	private void get()
	{
		if ( wTabFolder.getSelection() == wFieldsTab )
		{
		    if (input.isUpdate()) getUpdate();
		    else getLookup();
		}
		else
		{
			getKeys();
		}
	}

	/**
	 * Get the fields from the previous step and use them as "update fields". 
	 * Only get the the fields which are not yet in use as key, or in 
	 * the field table. Also ignore technical key, version, fromdate, todate.
	 */
	private void getUpdate()
	{
		try
		{
			Row r = transMeta.getPrevStepFields(stepname);
			if (r!=null)
			{
				Table table=wUpIns.table;
				for (int i=0;i<r.size();i++)
				{
					Value v = r.getValue(i);
					int idx = wKey.indexOfString(v.getName(), 2);
					int idy = wUpIns.indexOfString(v.getName(), 2);
					if (idx<0 && idy<0)
					{
						TableItem ti = new TableItem(table, SWT.NONE);
						ti.setText(1, v.getName());
						ti.setText(2, v.getName());
						ti.setText(3, Messages.getString("DimensionLookupDialog.TableItem.Insert.Label")); //$NON-NLS-1$
					}
				}
				wUpIns.removeEmptyRows();
				wUpIns.setRowNums();
				wUpIns.optWidth(true);
			}
		}
		catch(KettleException ke)
		{
			new ErrorDialog(shell, props, Messages.getString("DimensionLookupDialog.FailedToGetFields.DialogTitle"), Messages.getString("DimensionLookupDialog.FailedToGetFields.DialogMessage"), ke); //$NON-NLS-1$ //$NON-NLS-2$
		}
	}

	/**
	 * Get the fields from the table in the database and use them as lookup 
	 * keys. Only get the the fields which are not yet in use as key, or in 
	 * the field table. Also ignore technical key, version, fromdate, todate.
	 */
	private void getLookup()
	{
		DatabaseMeta ci = transMeta.findDatabase(wConnection.getText());
		if (ci!=null)
		{
			Database db = new Database(ci);
			try
			{
				db.connect();
				Row r = db.getTableFields(wTable.getText());
				if (r!=null)
				{
					Table table=wUpIns.table;
					for (int i=0;i<r.size();i++)
					{
						Value v = r.getValue(i);
						int idx = wKey.indexOfString(v.getName(), 2);
						int idy = wUpIns.indexOfString(v.getName(), 2);
						if (idx<0 &&
							idy<0 &&
							!v.getName().equalsIgnoreCase(wTk.getText()) &&
							!v.getName().equalsIgnoreCase(wVersion.getText()) &&
							!v.getName().equalsIgnoreCase(wFromdate.getText()) &&
							!v.getName().equalsIgnoreCase(wTodate.getText())
							)
						{
							TableItem ti = new TableItem(table, SWT.NONE);
							ti.setText(1, v.getName());
							ti.setText(2, v.getName());
							ti.setText(3, v.getTypeDesc());
						}
					}
					wUpIns.removeEmptyRows();
					wUpIns.setRowNums();
					wUpIns.optWidth(true);
				}
			}
			catch(KettleException e)
			{
				MessageBox mb = new MessageBox(shell, SWT.OK | SWT.ICON_ERROR );
				mb.setText(Messages.getString("DimensionLookupDialog.ErrorOccurred.DialogTitle")); //$NON-NLS-1$
				mb.setMessage(Messages.getString("DimensionLookupDialog.ErrorOccurred.DialogMessage")+Const.CR+e.getMessage()); //$NON-NLS-1$
				mb.open();
			}
			finally
			{
				db.disconnect();
			}
		}
	}

	/**
	 * Get the fields from the previous step and use them as "keys". Only 
	 * get the the fields which are not yet in use as key, or in the field 
	 * table. Also ignore technical key, version, fromdate, todate.
	 */
	private void getKeys()
	{
		try
		{
			Row r = transMeta.getPrevStepFields(stepname);
			if (r!=null)
			{
				Table table=wKey.table;
				for (int i=0;i<r.size();i++)
				{
					Value v = r.getValue(i);
					int idx = wKey.indexOfString(v.getName(), 2);
					int idy = wUpIns.indexOfString(v.getName(), 2);
					if (idx<0 &&
						idy<0 &&
						!v.getName().equalsIgnoreCase(wTk.getText()) &&
						!v.getName().equalsIgnoreCase(wVersion.getText()) &&
						!v.getName().equalsIgnoreCase(wFromdate.getText()) &&
						!v.getName().equalsIgnoreCase(wTodate.getText())
						)
					{
						TableItem ti = new TableItem(table, SWT.NONE);
						ti.setText(1, v.getName());
						ti.setText(2, v.getName());
						ti.setText(3, v.getTypeDesc());
					}
				}
				wKey.removeEmptyRows();
				wKey.setRowNums();
				wKey.optWidth(true);				
			}
		}
		catch(KettleException ke)
		{
			new ErrorDialog(shell, props, Messages.getString("DimensionLookupDialog.FailedToGetFields.DialogTitle"), Messages.getString("DimensionLookupDialog.FailedToGetFields.DialogMessage"), ke); //$NON-NLS-1$ //$NON-NLS-2$
		}		
	}	
	
	// Generate code for create table...
	// Conversions done by Database
	// For Sybase ASE: don't keep everything in lowercase!
	private void create()
	{
		try
		{
			DimensionLookupMeta info = new DimensionLookupMeta();
			getInfo(info);

			String name = stepname;  // new name might not yet be linked to other steps!
			StepMeta stepinfo = new StepMeta(log, Messages.getString("DimensionLookupDialog.Stepinfo.Title"), name, info); //$NON-NLS-1$
			Row prev = transMeta.getPrevStepFields(stepname);

			SQLStatement sql = info.getSQLStatements(transMeta, stepinfo, prev);
			if (!sql.hasError())
			{
				if (sql.hasSQL())
				{
					SQLEditor sqledit = new SQLEditor(shell, SWT.NONE, info.getDatabaseMeta(), transMeta.getDbCache(), sql.getSQL());
					sqledit.open();
				}
				else
				{
					MessageBox mb = new MessageBox(shell, SWT.OK | SWT.ICON_INFORMATION );
					mb.setMessage(Messages.getString("DimensionLookupDialog.NoSQLNeeds.DialogMessage")); //$NON-NLS-1$
					mb.setText(Messages.getString("DimensionLookupDialog.NoSQLNeeds.DialogTitle")); //$NON-NLS-1$
					mb.open();
				}
			}
			else
			{
				MessageBox mb = new MessageBox(shell, SWT.OK | SWT.ICON_ERROR );
				mb.setMessage(sql.getError());
				mb.setText(Messages.getString("DimensionLookupDialog.SQLError.DialogTitle")); //$NON-NLS-1$
				mb.open();
			}
		}
		catch(KettleException ke)
		{
			new ErrorDialog(shell, props, Messages.getString("DimensionLookupDialog.UnableToBuildSQLError.DialogMessage"), Messages.getString("DimensionLookupDialog.UnableToBuildSQLError.DialogTitle"), ke); //$NON-NLS-1$ //$NON-NLS-2$
		}
	}

	public String toString()
	{
		return this.getClass().getName();
	}
}
