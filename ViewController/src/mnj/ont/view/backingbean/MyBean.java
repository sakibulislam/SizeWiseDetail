package mnj.ont.view.backingbean;

import java.io.BufferedReader;

import java.io.BufferedWriter;
import java.io.IOException;

import java.io.InputStreamReader;

import java.io.OutputStream;
import java.io.OutputStreamWriter;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

import java.sql.SQLException;

import java.util.HashMap;
import java.util.StringTokenizer;

import javax.faces.application.FacesMessage;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import mnj.ont.model.services.AppModuleImpl;

import oracle.adf.model.BindingContext;
import oracle.adf.model.binding.DCBindingContainer;
import oracle.adf.model.binding.DCDataControl;
import oracle.adf.model.binding.DCIteratorBinding;
import oracle.adf.view.rich.component.rich.data.RichTable;
import oracle.adf.view.rich.component.rich.input.RichInputText;
import oracle.adf.view.rich.context.AdfFacesContext;

import oracle.adfinternal.view.faces.bi.util.JsfUtils;

import oracle.binding.BindingContainer;
import oracle.binding.OperationBinding;

import oracle.jbo.ApplicationModule;
import oracle.jbo.Row;

import oracle.jbo.RowSetIterator;
import oracle.jbo.ViewObject;
import oracle.jbo.uicli.binding.JUCtrlHierBinding;

import oracle.jdbc.OracleTypes;

import org.apache.myfaces.trinidad.model.CollectionModel;
import org.apache.myfaces.trinidad.model.UploadedFile;
import org.apache.myfaces.trinidad.render.ExtendedRenderKitService;
import  org.apache.myfaces.trinidad.util.Service;

public class MyBean {
    private RichTable uploadTable;
    private RichInputText buyerId;
    private RichInputText inseamQty;
    private RichTable inseamTable;
    private RichInputText orderQty;
    private RichTable sizeDetailBind;

    public MyBean() {
    }
    
    public ApplicationModule getAppM(){
        DCBindingContainer bindingContainer =
            (DCBindingContainer)BindingContext.getCurrent().getCurrentBindingsEntry();
        //BindingContext bindingContext = BindingContext.getCurrent();
        DCDataControl dc =
            bindingContainer.findDataControl("AppModuleDataControl"); // Name of application module in datacontrolBinding.cpx
        AppModuleImpl appM = (AppModuleImpl)dc.getDataProvider();
        return appM;
    }
    AppModuleImpl am = (AppModuleImpl)this.getAppM();

    public void fileUploadBPO(ValueChangeEvent valueChangeEvent) {
        // Add event code here...
        UploadedFile file = (UploadedFile)valueChangeEvent.getNewValue();
        try {
            //AdfFacesContext.getCurrentInstance().addPartialTarget(uploadTable);
            parseFile(file.getInputStream());

        } catch (IOException e) {
            // TODO add more
        }
    }

    public void parseFile(java.io.InputStream file) {


        System.out.println("Parse File --->" + file);

        BufferedReader reader =
            new BufferedReader(new InputStreamReader(file));
        String strLine = "";
        StringTokenizer st = null;
        int lineNumber = 0, tokenNumber = 0;
        Row hrw = null, lineRow = null;
        oracle.jbo.domain.Number headerSeq = new oracle.jbo.domain.Number();
        oracle.jbo.domain.Number lineSeq = new oracle.jbo.domain.Number();

        //        CollectionModel _tableModel = (CollectionModel)uploadTable.getValue();
        //        //the ADF object that implements the CollectionModel is JUCtrlHierBinding. It
        //        //is wrapped by the CollectionModel API
        //        JUCtrlHierBinding _adfTableBinding =
        //            (JUCtrlHierBinding)_tableModel.getWrappedData();
        //        //Acess the ADF iterator binding that is used with ADF table binding
        //        DCIteratorBinding it = _adfTableBinding.getDCIteratorBinding();

        //read comma separated file line by line

        HashMap map = new HashMap();
        BindingContext bindingContext = BindingContext.getCurrent();
        DCDataControl dc =
            bindingContext.findDataControl("AppModuleDataControl");
        ApplicationModule am = dc.getApplicationModule();
        ViewObject hvo = am.findViewObject("MnjOntSizewiseInterfaceHView1");
        ViewObject lineVo = am.findViewObject("MnjOntSizewiseInterfaceLView1");


        try {
            while ((strLine = reader.readLine()) != null) {
                lineNumber++;
                // create a new row skip the header  (header has linenumber 1)


                //break comma separated line using ","
                st = new StringTokenizer(strLine, ",");
                System.out.println("All Toekens l--->" + st.countTokens());


                if (lineNumber > 1) {
                    hrw = hvo.createRow();
                    hrw.setNewRowState(Row.STATUS_INITIALIZED);
                    hvo.insertRow(hrw);

                    oracle.jbo.server.SequenceImpl s =
                        new oracle.jbo.server.SequenceImpl("MNJ_ONT_SIZEWISE_INTERFACE_H_S",
                                                           am);
                    headerSeq = s.getSequenceNumber();
                    hrw.setAttribute("HeaderId", headerSeq);
                }

                //System.out.println("View object --->" + hvo);

                while (st.hasMoreTokens()) {
                    //display csv values
                    tokenNumber++;


                    String theToken = st.nextToken();
                    //                    System.out.println("Line # " + lineNumber + ", Token # " +
                    //                                       tokenNumber + ", Token : " + theToken);

                    if (lineNumber == 1 && tokenNumber > 7) {
                        map.put(tokenNumber, theToken); //6, 92
                    }

                    if (lineNumber > 1) {

                        switch (tokenNumber) {
                        case 1:
                            hrw.setAttribute("BpoNumber",
                                             theToken); //DeliveryDate
                        case 2:
                            hrw.setAttribute("DeliveryDate", theToken);
                        case 3:
                            hrw.setAttribute("DeliveryTerm", theToken);
                        case 4:
                            hrw.setAttribute("ShipMode", theToken);
                        case 5:
                            hrw.setAttribute("StyleWash", theToken);
                        case 6:
                            hrw.setAttribute("Color", theToken); //StyleWash
                        case 7:
                            hrw.setAttribute("Dcpo", theToken);


                        }
                        if (tokenNumber > 7) {
                            lineRow = lineVo.createRow();
                            lineRow.setNewRowState(Row.STATUS_INITIALIZED);
                            lineVo.insertRow(lineRow);

                            oracle.jbo.server.SequenceImpl s =
                                new oracle.jbo.server.SequenceImpl("MNJ_ONT_SIZEWISE_INTERFACE_L_S",
                                                                   am);
                            lineSeq = s.getSequenceNumber();
                            lineRow.setAttribute("LineId",
                                                 lineSeq); //Set Primary Key
                            lineRow.setAttribute("HeaderId",
                                                 headerSeq); //Set Foregin Key

                            lineRow.setAttribute("SizeInseam",
                                                 map.get(tokenNumber));
                            lineRow.setAttribute("Qty", theToken);

                            /**Put Inseam in header******************************/
                            hrw.setAttribute("Inseam", theToken);
                        } // end of inner if


                    } //end of outer if
                } //end of inner loop
                //reset token number
                tokenNumber = 0;
            } //end of outer loop
            map = null;
            reader = null;

        } catch (Exception e) {
            FacesContext fctx = FacesContext.getCurrentInstance();
            fctx.addMessage(null,
                            new FacesMessage(FacesMessage.SEVERITY_ERROR, "Data Error in Uploaded file",
                                             e.getMessage()));

        }
        saveFile();

    } //END OF METHOD

    public void setUploadTable(RichTable uploadTable) {
        this.uploadTable = uploadTable;
    }

    public RichTable getUploadTable() {
        return uploadTable;
    }


    public BindingContainer getBindings() {
        return BindingContext.getCurrent().getCurrentBindingsEntry();
    }

    public String saveFile() {

        save();

        OperationBinding operationBinding = executeOperation("callSplitSizes");
        operationBinding.execute();

        //invoke method
        operationBinding.execute();
        if (!operationBinding.getErrors().isEmpty()) {
            System.out.println("if errors-->");
            // List errors = operationBinding.getErrors();
        }
        //optional
        Object methodReturnValue = operationBinding.getResult();
        String message = null;
        if (methodReturnValue != null) {
            message = methodReturnValue.toString();
        } else {
            message = "Failed!";
        }
        System.out.println("Message ----->"+message);
        FacesMessage fm = new FacesMessage(message);
        fm.setSeverity(FacesMessage.SEVERITY_INFO);
        FacesContext context = FacesContext.getCurrentInstance();
        context.addMessage(null, fm);

        return null;
    }


    public void clearInterface() {


        CollectionModel _tableModel = (CollectionModel)uploadTable.getValue();
        //the ADF object that implements the CollectionModel is JUCtrlHierBinding. It
        //is wrapped by the CollectionModel API
        JUCtrlHierBinding _adfTableBinding =
            (JUCtrlHierBinding)_tableModel.getWrappedData();
        //Acess the ADF iterator binding that is used with ADF table binding
        DCIteratorBinding it = _adfTableBinding.getDCIteratorBinding();
        ViewObject vo = it.getViewObject();
        RowSetIterator it2 = vo.createRowSetIterator("a");
        while (it2.hasNext())
            it2.next().remove();
        it2.closeRowSetIterator();

        save();

    }


    public void save() {

        OperationBinding ob = executeOperation("Commit");
        ob.execute();

    }

    /*****Generic Method to Get BindingContainer**/
    public BindingContainer getBindingsCont() {
        return BindingContext.getCurrent().getCurrentBindingsEntry();
    }

    /**
     * Generic Method to execute operation
     * */
    public OperationBinding executeOperation(String operation) {
        OperationBinding createParam =
            getBindingsCont().getOperationBinding(operation);
        return createParam;

    }


    public String matrixReport() {
        OperationBinding ob = executeOperation("callMatrix");
        ob.execute();

        Object methodReturnValue = ob.getResult();
        String message = null;
        if (methodReturnValue != null) {
            message = methodReturnValue.toString();
        } else {
            message = "Failed!";
        }
        FacesMessage fm = new FacesMessage(message);
        fm.setSeverity(FacesMessage.SEVERITY_INFO);
        FacesContext context = FacesContext.getCurrentInstance();
        context.addMessage(null, fm);

        return null;
    }

    public void fileUploadDCPO(ValueChangeEvent valueChangeEvent) {
        // Add event code here...
        UploadedFile file = (UploadedFile)valueChangeEvent.getNewValue();
        try {
            //AdfFacesContext.getCurrentInstance().addPartialTarget(uploadTable);
            parseFileDCPO(file.getInputStream());

        } catch (IOException e) {
            // TODO add more
        }
    }

    public void parseFileDCPO(java.io.InputStream file) {


        System.out.println("Parse File --->" + file);

        BufferedReader reader =
            new BufferedReader(new InputStreamReader(file));
        String strLine = "";
        StringTokenizer st = null;
        int lineNumber = 0, tokenNumber = 0;
        Row hrw = null, lineRow = null;
        oracle.jbo.domain.Number headerSeq = new oracle.jbo.domain.Number();
        oracle.jbo.domain.Number lineSeq = new oracle.jbo.domain.Number();

        //        CollectionModel _tableModel = (CollectionModel)uploadTable.getValue();
        //        //the ADF object that implements the CollectionModel is JUCtrlHierBinding. It
        //        //is wrapped by the CollectionModel API
        //        JUCtrlHierBinding _adfTableBinding =
        //            (JUCtrlHierBinding)_tableModel.getWrappedData();
        //        //Acess the ADF iterator binding that is used with ADF table binding
        //        DCIteratorBinding it = _adfTableBinding.getDCIteratorBinding();

        //read comma separated file line by line

        HashMap map = new HashMap();
        BindingContext bindingContext = BindingContext.getCurrent();
        DCDataControl dc =
            bindingContext.findDataControl("AppModuleDataControl");
        ApplicationModule am = dc.getApplicationModule();
        ViewObject hvo = am.findViewObject("MnjOntSizewiseInterfaceHView1");
        ViewObject lineVo = am.findViewObject("MnjOntSizewiseInterfaceLView1");


        try {
            while ((strLine = reader.readLine()) != null) {
                lineNumber++;
                // create a new row skip the header  (header has linenumber 1)


                //break comma separated line using ","
                st = new StringTokenizer(strLine, ",");
                System.out.println("All Toekens l--->" + st.countTokens());


                if (lineNumber > 1) {
                    hrw = hvo.createRow();
                    hrw.setNewRowState(Row.STATUS_INITIALIZED);
                    hvo.insertRow(hrw);

                    oracle.jbo.server.SequenceImpl s =
                        new oracle.jbo.server.SequenceImpl("MNJ_ONT_SIZEWISE_INTERFACE_H_S",
                                                           am);
                    headerSeq = s.getSequenceNumber();
                    hrw.setAttribute("HeaderId", headerSeq);
                }

                //System.out.println("View object --->" + hvo);

                while (st.hasMoreTokens()) {
                    //display csv values
                    tokenNumber++;


                    String theToken = st.nextToken();

                    if (lineNumber == 1 && tokenNumber > 7) {
                        map.put(tokenNumber, theToken); //6, 92
                    }

                    if (lineNumber > 1) {

                        switch (tokenNumber) {
                        case 1:
                            hrw.setAttribute("BpoNumber",
                                             theToken); //DeliveryDate
                        case 2:
                            hrw.setAttribute("DeliveryDate", theToken);
                        case 3:
                            hrw.setAttribute("DeliveryTerm", theToken);
                        case 4:
                            hrw.setAttribute("ShipMode", theToken);
                        case 5:
                            hrw.setAttribute("StyleWash", theToken);
                        case 6:
                            hrw.setAttribute("Color", theToken); //StyleWash
                        case 7:
                            hrw.setAttribute("Dcpo", theToken);


                        }
                        if (tokenNumber > 7) {
                            lineRow = lineVo.createRow();
                            lineRow.setNewRowState(Row.STATUS_INITIALIZED);
                            lineVo.insertRow(lineRow);

                            oracle.jbo.server.SequenceImpl s =
                                new oracle.jbo.server.SequenceImpl("MNJ_ONT_SIZEWISE_INTERFACE_L_S",
                                                                   am);
                            lineSeq = s.getSequenceNumber();
                            lineRow.setAttribute("LineId",
                                                 lineSeq); //Set Primary Key
                            lineRow.setAttribute("HeaderId",
                                                 headerSeq); //Set Foregin Key

                            lineRow.setAttribute("SizeInseam",
                                                 map.get(tokenNumber));
                            lineRow.setAttribute("Qty", theToken);

                            /**Put Inseam in header******************************/
                            hrw.setAttribute("Inseam", theToken);
                        } // end of inner if


                    } //end of outer if
                } //end of inner loop
                //reset token number
                tokenNumber = 0;
            } //end of outer loop
            map = null;
            reader = null;

        } catch (Exception e) {
            FacesContext fctx = FacesContext.getCurrentInstance();
            fctx.addMessage(null,
                            new FacesMessage(FacesMessage.SEVERITY_ERROR, "Data Error in Uploaded file",
                                             e.getMessage()));

        }
        saveFileDCPO();

    } //END OF METHOD

    public String saveFileDCPO() {

        save();

        OperationBinding operationBinding = executeOperation("uploadDCPOs");
        operationBinding.execute();

        //invoke method
        operationBinding.execute();
        if (!operationBinding.getErrors().isEmpty()) {
            System.out.println("if errors-->");
            // List errors = operationBinding.getErrors();
        }
        //optional
        Object methodReturnValue = operationBinding.getResult();
        String message = null;
        if (methodReturnValue != null) {
            message = methodReturnValue.toString();
        } else {
            message = "Failed!";
        }
        FacesMessage fm = new FacesMessage(message);
        fm.setSeverity(FacesMessage.SEVERITY_INFO);
        FacesContext context = FacesContext.getCurrentInstance();
        context.addMessage(null, fm);

        return null;
    }


    public void setBuyerId(RichInputText buyerId) {
        this.buyerId = buyerId;

        FacesContext fctx = FacesContext.getCurrentInstance();
        ExternalContext ectx = fctx.getExternalContext();
        HttpSession userSession = (HttpSession)ectx.getSession(false);
        userSession.setAttribute("buyerId", buyerId.getValue());
        System.out.println("Buyer id -->" + buyerId.getValue());

    }

    public RichInputText getBuyerId() {
        return buyerId;
    }


    public void setInseamQty(RichInputText inseamQty) {
        this.inseamQty = inseamQty;
    }

    public RichInputText getInseamQty() {
        return inseamQty;
    }

    public void setInseamTable(RichTable inseamTable) {
        this.inseamTable = inseamTable;
    }

    public RichTable getInseamTable() {
        return inseamTable;
    }

    public void inseamQtyListener(ValueChangeEvent valueChangeEvent) {
        // Add event code here...
/*
        int crntVal =
            Integer.parseInt(valueChangeEvent.getNewValue().toString());
        boolean flag = validateInsemaQty(crntVal);

        if (flag) {

            FacesMessage fm =
                new FacesMessage("BPO or sum of DCPO qty should not be greater than Order Total Qty");
            fm.setSeverity(FacesMessage.SEVERITY_ERROR);
            FacesContext context = FacesContext.getCurrentInstance();
            context.addMessage(valueChangeEvent.getComponent().getClientId(context),
                               fm);
            inseamQty.resetValue();

        }

        else {

        }*/
    }

    public boolean validateInsemaQty(int newValueCrntRow) {

        oracle.adf.view.rich.component.UIXTable table = getInseamTable();
        // Get the Selected Row key set iterator
        java.util.Iterator selectionIt = table.getSelectedRowKeys().iterator();
        int size = 0;
        int bpoTotal = 0, dcpoTotal = 0;
        int orderQtyVal =
            Integer.parseInt(getOrderQty().getValue().toString());
        boolean flag = false;


        BindingContext bindingContext = BindingContext.getCurrent();
        DCDataControl dc =
            bindingContext.findDataControl("AppModuleDataControl"); //
        ApplicationModule am = dc.getApplicationModule();
        ViewObject findViewObject =
            am.findViewObject("CustMnjOntSoObinSizlineLINESView1");
        Row curRow = findViewObject.getCurrentRow();
        RowSetIterator it = findViewObject.createRowSetIterator("a");
        String type = null;
        System.out.println("current Row ---->" + curRow);
        while (it.hasNext()) {
            Row r = it.next();
            try {
                type = getType(r);
                size =
Integer.parseInt(r.getAttribute("InseamQty").toString());
            } catch (Exception e) {
                size = 0;
            }

            if (curRow.equals(r)) {
                size = newValueCrntRow;
            }

            if (type.equalsIgnoreCase("BPO Line")) {
                bpoTotal = bpoTotal + size;
            } else {
                dcpoTotal = dcpoTotal + size;
            }
        }
        it.closeRowSetIterator();

        if (bpoTotal > orderQtyVal || dcpoTotal > orderQtyVal) {
            flag = true;
        } else {
            flag = false;
        }

        return flag;

    }

    public String getType(Row r) {

        String type = null;
        try {
            type = r.getAttribute("DcpoNo").toString();
        } catch (Exception e) {
            type = "DCPO";

        }
        return type;
    }


    public void setOrderQty(RichInputText orderQty) {
        this.orderQty = orderQty;
    }

    public RichInputText getOrderQty() {
        return orderQty;
    }

    public void validateOrderedQty() {
//System.out.println("Level Order Qty 1");
            oracle.adf.view.rich.component.UIXTable table = getInseamTable();
            // Get the Selected Row key set iterator
            java.util.Iterator selectionIt = table.getSelectedRowKeys().iterator();
            int orderQtyVal = Integer.parseInt(getOrderQty().getValue().toString());
            int TotalInseamQty=0;
      //  System.out.println("Level Order Qty 2");

            BindingContext bindingContext = BindingContext.getCurrent();
            DCDataControl dc =bindingContext.findDataControl("AppModuleDataControl"); 
            ApplicationModule am = dc.getApplicationModule();
            ViewObject findViewObject =am.findViewObject("CustMnjOntSoObinSizlineLINESView1");
            RowSetIterator it = findViewObject.createRowSetIterator("a");

    //    System.out.println("Level Order Qty 3");

            while (it.hasNext()) {
                Row r = it.next();
                try {
                    TotalInseamQty+=Integer.parseInt(r.getAttribute("InseamQty").toString());

                } catch (Exception e) {;
                  }
            }
            it.closeRowSetIterator();
    
  //      System.out.println(TotalInseamQty+"<<----Total Inseam Qty--->>"+orderQtyVal);
    
        if (orderQtyVal!=TotalInseamQty) {
            System.out.println(TotalInseamQty+"<<----Total Inseam Qty 1243--->>"+orderQtyVal);
            String message="Total Line Quantity and STN Quantity are not equal.";
            FacesMessage fm = new FacesMessage(message);
            fm.setSeverity(FacesMessage.SEVERITY_WARN);
            FacesContext context = FacesContext.getCurrentInstance();
            context.addMessage(null, fm);
        
        }
        
    }

    public void SaveForm(ActionEvent actionEvent) {
        // Add event code here...
        try {
            System.out.println("First Line------>>");
            validateOrderedQty();
            
            
            System.out.println("Second Last Line------>>");
            if(getSizeQtyCheck()==true){
                Save();
            }else{
                String message="Line Quantity and STN Quantity are not equal.";
                FacesMessage fm = new FacesMessage(message);
                fm.setSeverity(FacesMessage.SEVERITY_WARN);
                FacesContext context = FacesContext.getCurrentInstance();
                context.addMessage(null, fm);
            }
            
            
            
            AdfFacesContext.getCurrentInstance().addPartialTarget(sizeDetailBind);
                        
            BindingContext bindingContext = BindingContext.getCurrent();
            DCDataControl dc =
                bindingContext.findDataControl("AppModuleDataControl");
            ApplicationModule am = dc.getApplicationModule();
            ViewObject hvo = am.findViewObject("CustMnjOntSoObinSizlineLINESView1");
            hvo.executeQuery();
            
            
            System.out.println("Last Line------>>");
        } catch (Exception e) {
            e.printStackTrace();
        }
  
    }

    public void Save() {
        BindingContainer bindings = getBindings();
        OperationBinding operationBinding = bindings.getOperationBinding("Commit");
        Object result = operationBinding.execute();}
        
    public String callAttachment() throws IOException {
      
        String doc= null;     
        BindingContext bindingContext = BindingContext.getCurrent(); 
        DCDataControl dc = bindingContext.findDataControl("AppModuleDataControl"); //
        ApplicationModule am  = dc.getApplicationModule() ;
        ViewObject findViewObject = am.findViewObject("HeaderVO1");
        
        try {
            doc = findViewObject.getCurrentRow().getAttribute("BpoNo").toString();
        } catch (Exception e) {
            // TODO: Add catch code
            ;
        }    
      
      
                String newPage =
                    "http://192.168.200.115:7003/FileUploading-ViewController-context-root/faces/view1?doc=SW&docNo="+doc;
                // String newPage = "http://localhost:7101/PurchaseMemo-ViewController-context-root/faces/SearchPG?headerId="+getBomId().getValue();
                FacesContext ctx = FacesContext.getCurrentInstance();
                ExtendedRenderKitService erks =
                    Service.getService(ctx.getRenderKit(), ExtendedRenderKitService.class);
                String url = "window.open('" + newPage + "','_blank','toolbar=no,location=no,menubar=no,alwaysRaised=yes,height=500,width=1100');";
                erks.addScript(FacesContext.getCurrentInstance(), url);
            
            
//        FacesContext fc = FacesContext.getCurrentInstance();
//        HttpServletResponse response = (HttpServletResponse)fc.getExternalContext().getResponse();
//        response.sendRedirect("http://192.168.200.115:7003/FileUploading-ViewController-context-root/faces/view1?doc=SW&docNo="+doc);
//        fc.responseComplete();
//            


        return null;
    }


    public void setSizeDetailBind(RichTable sizeDetailBind) {
        this.sizeDetailBind = sizeDetailBind;
    }

    public RichTable getSizeDetailBind() {
        return sizeDetailBind;
    }
    
    
    public void sizeUpload(ValueChangeEvent valueChangeEvent) {
      // Add event code here...
          UploadedFile file = (UploadedFile)valueChangeEvent.getNewValue();
          try {
              clearSizeBreakDownTable();
              parseFile2(file.getInputStream());
              AdfFacesContext.getCurrentInstance().addPartialTarget(sizeDetailBind);
              am.getDBTransaction().commit();
          } catch (IOException e) {
          // TODO
          }
      }
      
      public void clearSizeBreakDownTable(){
          
          am.getDBTransaction().commit();
          
          ViewObject vo =  am.getCustMnjOntSoObinslineDetailView1();
          RowSetIterator it = vo.createRowSetIterator("aa");
          while(it.hasNext()){
              it.next().remove();
          }
          vo.executeEmptyRowSet();
         it.closeRowSetIterator();
          
      }
      
      public void parseFile2(java.io.InputStream file) {
          BufferedReader reader = new BufferedReader(new InputStreamReader(file));
          String strLine = "";
          StringTokenizer st = null;
          int lineNumber = 0, tokenNumber = 0;
          Row rw = null;
           
          ViewObject vo =  am.getCustMnjOntSoObinslineDetailView1();
          
          //read comma separated file line by line
          try
          {
              while ((strLine = reader.readLine()) != null)
              {
                  lineNumber++;
                  // create a new row skip the header (header has linenumber 1)
                  if (lineNumber>1) {
                      rw = vo.createRow();
                      rw.setNewRowState(Row.STATUS_INITIALIZED);
                      vo.insertRow(rw);
                  }
                   
                  //break comma separated line using ","
                  st = new StringTokenizer(strLine, ",");
                      
                  double sizeProjQty=0, sizeActualQty=0,addDeductQty;
                      
                  while (st.hasMoreTokens())
                  {
                      //display csv values
                      tokenNumber++;
                       
                      String theToken = st.nextToken();
                      System.out.println("Line # " + lineNumber + ", Token # " +
                      tokenNumber +
                      ", Token : " + theToken);
                      
                      if (lineNumber>1){
                          // set Attribute Values
                          switch (tokenNumber) {
                              case 1: rw.setAttribute("InseamSizeConcat", theToken);
                                      break;
                              case 2: rw.setAttribute("SizeQty", theToken);
                                      break;
                                     
                          }
                      }
                  }
                  //reset token number
                  tokenNumber = 0;
              }
          }
          catch (IOException e) {
          // TODO add more
              FacesContext fctx = FacesContext.getCurrentInstance();
              fctx.addMessage(sizeDetailBind.getClientId(fctx), new FacesMessage(FacesMessage.SEVERITY_ERROR,
              "Content Error in Uploaded file", e.getMessage()));
          }
          catch (Exception e) {
          FacesContext fctx = FacesContext.getCurrentInstance();
          fctx.addMessage( null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
          "Data Error in Uploaded file", e.getMessage()));
          }
      }
      
    /*************************************************************
     * Update Sale Orders lines
     * **********************************/

    public void updateQty(ActionEvent actionEvent) {
        // Add event code here...
        ViewObject hvo=am.getHeaderVO1();
        String bpo=hvo.getCurrentRow().getAttribute("BpoNo").toString();
        String buyer=hvo.getCurrentRow().getAttribute("CustomerId").toString();
        String ssn=hvo.getCurrentRow().getAttribute("Season").toString();
        String styleName=hvo.getCurrentRow().getAttribute("StyleName").toString();
        String styleNo=hvo.getCurrentRow().getAttribute("StyleNo").toString();
        
        String query= "select mmpl.HEADER_ID from mnj_mfg_precosting_l mmpl,mnj_mfg_precosting_h mmph \n" + 
                    "where \n" + 
                    "mmpl.HEADER_ID=mmph.HEADER_ID\n" +
                    "and mmpl.BPO_NO=?";
//                    "and mmpl.BPO_NO=? \n" + 
//                    "and mmph.BUYER_ID=?\n" + 
//                    "and mmph.SEASON=?\n" + 
//                    "and (mmph.STYLE_NAME_NEW=?\n" + 
//                    "or mmph.STYLE_NO=?)";
        
         ResultSet resultSet=null;                                                                                 
        PreparedStatement createStatement= am.getDBTransaction().createPreparedStatement(query,0);
        String headerId="";
        try {
            System.out.println("Problem1");
            createStatement.setString(1, bpo);
        
//        createStatement.setString(2, buyer);
//        createStatement.setString(3, ssn);
//        createStatement.setString(4, styleName);
//        createStatement.setString(5, styleNo);
//     
             System.out.println("Problem2");
        resultSet=createStatement.executeQuery();
         System.out.println("Problem3");
        headerId=resultSet.getString("HEADER_ID"); 
            System.out.println("Problem4");
        } catch (SQLException e) {
            System.out.println(e);
        }
        
        
        System.out.println("Header Id in AM -->" + headerId+"--BPO--"+bpo);
        String value = null;
        String stmt =
            "BEGIN  Create_PreCostSO_PKG.Update_PreCost_Line_SO(:1,:2); end;";
        java.sql.CallableStatement cs =
            am.getDBTransaction().createCallableStatement(stmt, 1);
        try {
            cs.registerOutParameter(2, OracleTypes.VARCHAR);
            cs.setString(1, headerId);
            cs.execute();
            value = cs.getString(2);
            cs.close();
        } catch (Exception e) {
            value = e.getMessage();
        }
        hvo.getCurrentRow().setAttribute("OrderedQuantity", value);
    }
    
    
    //=============================================================added by farabi==========excel upload==========20.07.2017=======================

    
    
     public void uploadMultipleBpo(ValueChangeEvent valueChangeEvent) {
      // Add event code here...
          UploadedFile file = (UploadedFile)valueChangeEvent.getNewValue();
          try {
              String msg = parseFile3(file.getInputStream());
              am.getDBTransaction().commit();
              
                ViewObject vo =  am.getCustMnjOntSizUploadTempView1();
                RowSetIterator it = vo.createRowSetIterator(null);
                while(it.hasNext()){
                    Row row = it.next();
                    String stmt =
                        "BEGIN  APPS.size_bpo_upload_from_temp(?); end;";
                    java.sql.CallableStatement cs =
                        am.getDBTransaction().createCallableStatement(stmt, 1);
                    try { 
                        cs.setString(1, row.getAttribute("TempId").toString());
                        cs.execute();
                        cs.close();
                    } catch (Exception e) {
                    }
                }
                vo.executeEmptyRowSet();
               it.closeRowSetIterator();
              am.getDBTransaction().commit();
              showMessage(msg);
             
          } catch (IOException e) {
          // TODO
          }
      }
      

      
      public String parseFile3(java.io.InputStream file) {
          BufferedReader reader = new BufferedReader(new InputStreamReader(file));
          String strLine = "";
          StringTokenizer st = null;
          int lineNumber = 0, tokenNumber = 0;
          Row rw = null;
          String msg="";
          String errMsg="";
          
          ViewObject vo =  am.getCustMnjOntSizUploadTempView1();
          
          //read comma separated file line by line
          try
          {
              while ((strLine = reader.readLine()) != null)
              {
                  lineNumber++;
                  // create a new row skip the header (header has linenumber 1)
                  if (lineNumber>1) {
                      rw = vo.createRow();
                      rw.setNewRowState(Row.STATUS_INITIALIZED);
                      vo.insertRow(rw);
                  }
                   
                  //break comma separated line using ","
                  
    //                  st = new StringTokenizer(strLine, ",");
    //                  while (st.hasMoreTokens())
    //                  {
    //                      //display csv values
    //                      tokenNumber++;
    //
    //                      String theToken = st.nextToken();
                String[] csvCols = strLine.split(",");   
                
                  
                for (;tokenNumber < csvCols.length ; tokenNumber++)
                {
                      String theToken = csvCols[tokenNumber];
                      System.out.println("Line # " + lineNumber + ", Token # " +
                      tokenNumber +
                      ", Token : " + theToken);
                      
                    
                      if (lineNumber>1){
                          // set Attribute Values
                          switch (tokenNumber) {
                              case 0: 
                                      String query= "SELECT distinct oh.cust_po_number exist FROM oe_order_headers_all oh WHERE oh.attribute1 = ? " +
                                                                                                                        "and oh.cust_po_number = ? ";
                                        ResultSet rs=null;  
                                        String isBPOExist = null;
                                        PreparedStatement createStatement= am.getDBTransaction().createPreparedStatement(query,0);
                                        createStatement.setString(1, theToken);
                                        tokenNumber++;
                                        String bpo = csvCols[tokenNumber];
                                        createStatement.setString(2, bpo);
                                        rs = createStatement.executeQuery();
                                        //rs = am.getDBTransaction().createStatement(0).executeQuery(query);
                                          if (rs.next()) {
                                              isBPOExist =  rs.getString(1);
                                          }
                                    //System.out.println("--------asdasd-----"+isBPOExist);
        
                                        if(isBPOExist == null){
                                            lineNumber++;
                                            rs.close();
                                            errMsg = "Error !! BPO - '" + bpo + "' not found !! ";    
                                            break;
                                        }
                                      rs.close();
                                      rw.setAttribute("Attribute1", theToken);
                                      rw.setAttribute("Bpo", bpo);
                                      break;
                              case 2: rw.setAttribute("Color", theToken);
                                      break;
                              case 3: rw.setAttribute("Country", theToken);
                                      break;
                              case 4: rw.setAttribute("DcpoCode", theToken);
                                      break;
                              case 5: rw.setAttribute("TotalQty", theToken);
                                      break;
                              case 6: rw.setAttribute("DeliveryDate", theToken);
                                      break;
                              case 7: rw.setAttribute("DeliveryTerm", theToken);
                                      break;
                              case 8: rw.setAttribute("ShipMode", theToken);
                                      break;
                              case 9: rw.setAttribute("SizeInseam", theToken);
                                      break;
                              case 10: rw.setAttribute("SizeQty", theToken);
                                      rw.setAttribute("UploadFlag", "N");
                                      break;
                                     
                          }
                      }
                  }
                  //reset token number
                  tokenNumber = 0;
              }
          }
          catch (IOException e) {
          // TODO add more
              FacesContext fctx = FacesContext.getCurrentInstance();
              fctx.addMessage(sizeDetailBind.getClientId(fctx), new FacesMessage(FacesMessage.SEVERITY_ERROR,
              "Content Error in Uploaded file", e.getMessage()));
          }
          catch (Exception e) {
          FacesContext fctx = FacesContext.getCurrentInstance();
          fctx.addMessage( null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
          "Data Error in Uploaded file", e.getMessage()));
          }
          msg = "Success !! Size breakdown uploaded !!";
          return  "<html><body><p style='color:red'><b>"+errMsg+"</b></p><p>"+msg+"</p></body></html>";
      }
      
    public String showMessage(String messageText) {
            FacesMessage fm = new FacesMessage(messageText);
            /**
             * set the type of the message.
             * Valid types: error, fatal,info,warning
             */
            fm.setSeverity(FacesMessage.SEVERITY_INFO);
            FacesContext context = FacesContext.getCurrentInstance();
            context.addMessage(null, fm);
            return null;
        }
    
    public void exportToCSV(FacesContext facesContext,
                            OutputStream outputStream) throws IOException {
        // Add event code here...
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream,"UTF-8"));
        
        writer.write("*Style No");writer.write(",");
        writer.write("*BPO");writer.write(",");
        writer.write("*Wash/Color");writer.write(",");
        writer.write("*Country");writer.write(",");
        writer.write("*DCPO");writer.write(",");
        writer.write("Total Size Qty");writer.write(",");
        writer.write("Delivery Date");writer.write(",");
        writer.write("Delivery Term");writer.write(",");
        writer.write("Shipment Mode");writer.write(",");
        writer.write("*Size");writer.write(",");
        writer.write("*Size Qty");
        writer.newLine();
        writer.flush();
        writer.close();
    }

    public String setBuyer() {
        // Add event code here...
        ViewObject sizeLineVO=am.getCustMnjOntSoObinSizlineLINESView1();
        ViewObject Header=am.getHeaderVO1();
        String custId=null;
        try{
            custId=Header.getCurrentRow().getAttribute("CustomerId").toString();
        }
        catch(Exception e){
            ;
        }
        System.out.println("the set buyer is= "+custId);
        sizeLineVO.getCurrentRow().setAttribute("CustId", custId);
       // sizeLineVO.executeQuery();
        return null;
    }
    
    public boolean getSizeQtyCheck(){
        
       ViewObject sizeDetailsVO= am.getCustMnjOntSoObinslineDetailView1();
       ViewObject lineVO=am.getCustMnjOntSoObinSizlineLINESView1();
        Row[] row=sizeDetailsVO.getAllRowsInRange();
        int qty=0,totalQty=0,lineQty=0;
        
        try{
           lineQty=Integer.parseInt(lineVO.getCurrentRow().getAttribute("InseamQty").toString()) ;
        }catch(Exception e){
            ;
        }
        
        for (Row r : row) {
            try{
                qty=Integer.parseInt(r.getAttribute("SizeQty").toString()) ;
            }catch(Exception e){
                ;
            }
                totalQty=totalQty+qty;
            }
        
        System.out.println("sizeDetailsQty====================>"+totalQty);
        
        if(totalQty>lineQty){
            return false;
        }else{
            return true;
        }
        
       
    }
}//end of class
