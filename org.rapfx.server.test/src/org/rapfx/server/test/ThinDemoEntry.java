package org.rapfx.server.test;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.rap.rwt.application.EntryPoint;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class ThinDemoEntry implements EntryPoint {

	@Override
	public int createUI() {
		Display display = new Display();
		final Shell shell = new Shell(display, SWT.SHELL_TRIM);

		shell.setText("heidy-hooo!");
		shell.setSize(400, 300);
		shell.setLayout(new FillLayout());

		Composite comp = new Composite(shell, SWT.NONE);
		comp.setLayout(new GridLayout());

		Composite c2 = new Composite(comp, SWT.NONE);
		c2.setBackground(new Color(display, 200, 200, 0));
		GridDataFactory.fillDefaults().grab(true, true).applyTo(c2);
		GridLayoutFactory.fillDefaults().applyTo(c2);
		
		Label l3 = new Label(c2, SWT.NONE);
		l3.setText("some short text to pack");
		l3.setBackground(new Color(display, 100,100,100));
		GridDataFactory.fillDefaults().align(SWT.CENTER, SWT.CENTER).applyTo(l3);

		Label lbl = new Label(comp, SWT.NONE);
		lbl.setText("hugooo");
		lbl.setBackground(new Color(display, 255, 0, 0));
		GridDataFactory.fillDefaults().grab(true, true).applyTo(lbl);

		Composite anotherComp = new Composite(comp, SWT.NONE);
		GridDataFactory.fillDefaults().grab(true, true).applyTo(anotherComp);
		GridLayoutFactory.fillDefaults().numColumns(2).applyTo(anotherComp);

		Button btn = new Button(anotherComp, SWT.PUSH);
		btn.setText("holla");
		btn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				createNewShell(shell);
			}
		});
		GridDataFactory.fillDefaults().align(SWT.CENTER, SWT.CENTER)
				.applyTo(btn);

		Label lbl2 = new Label(anotherComp, SWT.WRAP);
		lbl2.setText("some more and longer text that should wrap a little. Maybe. Hopefully.");
		GridDataFactory.fillDefaults().grab(true, false)
				.align(SWT.FILL, SWT.CENTER).applyTo(lbl2);
		
		Button da = new Button(anotherComp, SWT.PUSH);
		da.setText("disabled");
		da.setEnabled(false);

		shell.open();
		while (true) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

	protected void createNewShell(Shell parent) {
		final Shell sh = new Shell(parent);
		sh.setSize(200, 200);
		sh.setLayout(new GridLayout());
		
		final Text txt = new Text(sh, SWT.BORDER);
		txt.setText("hugo");
		
		Button btn = new Button(sh, SWT.PUSH);
		btn.setText("close me");
		btn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				System.out.println(txt.getText());
				sh.close();
			}
		});
		
		sh.open();
	}
}
