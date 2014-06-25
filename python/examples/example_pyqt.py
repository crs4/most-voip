#!/usr/bin/python

import sys
import logging
from PyQt4 import QtGui, QtCore  

from most.voip.api import VoipLib
from most.voip.constants import VoipEvent, VoipEventType


logger = None

class MostVoipGUI(QtGui.QMainWindow):
    
    
        
    def __init__(self):
        QtGui.QMainWindow.__init__(self)
        self.setWindowTitle('Most Voip Demo Application')
        self._setup_logger()
        self.myVoip = VoipLib()
        self.buddies = []
        self._build_GUI()
        
    def _setup_logger(self):
        global logger
        if not logger:
            logger = logging.getLogger("VoipDemo") #('Voip')
            
            handler = logging.StreamHandler()
    #         rootFormatter = logging.Formatter('%(name)s - %(levelname)s: %(msg)s')
    #         handler.setFormatter(rootFormatter)
            logger.addHandler(handler)
            logger.setLevel(logging.DEBUG)
            #print "NUM LOGGER HANDLERS:%s" % len(logger.handlers)
        
    def notify_events(self, voip_event_type,voip_event, params):
        msg = "%s: %s" % (voip_event_type,voip_event)
        logger.debug("\n\nEVENT:%s\n\n" % msg)
        self.statusBar().showMessage(msg)
        
        self._update_status_labels()
        
        if voip_event==VoipEvent.LIB_INITIALIZED:
            self.myVoip.register_account();
        elif voip_event==VoipEvent.ACCOUNT_REGISTERED:
            self._add_buddies()
            self._update_buddy_list()
        elif voip_event_type==VoipEventType.BUDDY_EVENT:
            self._update_buddy_list()
            
        

    def _add_buddies(self):
        buddy_extensions = ["steand", "ste2"]
        logger.debug("Adding buddies...")
        for ext in buddy_extensions:
            self.myVoip.add_buddy(ext)
        
    def _update_buddy_list(self):
        buddies = self.myVoip.get_buddies()
        logger.debug("Update Buddy Model...")
        for b in buddies:
            msg = "%s (%s)" % (b.get_uri().encode(), b.get_status_text().encode())
            logger.debug("Appending buddy:%s" % msg)
            #item = QtGui.QStandardItem(msg)
            #self.buddiesModel.appendRow(item)
        
            
 
    
        
    def get_init_params(self):
        voip_params0 = {u'username': u'ste', 
                   u'sip_pwd': u'ste', 
                   u'sip_server': u'156.148.33.226' , #'u'192.168.1.79',  u'156.148.33.223' 
                   u'sip_user': u'ste', 
                   u'transport' :u'udp',
                   #u'turn_server': u'192.168.1.79', 
                   #u'turn_user': u'', 
                   #u'turn_pwd': u'',
                   u'log_level' : 1,
                   u'debug' : True }   
        return voip_params0
    
    def init_voip_lib(self):
        print "Called init_voip_lib"
        self.voip_params = self.get_init_params()
        self.myVoip.init_lib(self.voip_params, self.notify_events)


    def _update_status_labels(self):
        self._update_server_state()
        self._update_call_state()
        self._update_account_state()
        
    def _update_server_state(self):
        server_state = self.myVoip.get_server().get_state()
        self.labServerStateInfo.setText(server_state)
        
    def _update_call_state(self):
        call_state = self.myVoip.get_call().get_state()
        self.labCallStateInfo.setText(call_state)
        
    def _update_account_state(self):
        account_state = self.myVoip.get_account().get_state()
        self.labAccountStateInfo.setText(account_state)
        
  


# -----------------------------------------------------------------------------
# GUI BUILDING SECTION
# -----------------------------------------------------------------------------
    
    def _build_GUI(self):
        cWidget = QtGui.QWidget(self)
        
        vBox = QtGui.QVBoxLayout()
        vBox.setSpacing(5)
        vBox.addLayout(self._buildStatesPanel(cWidget))
        vBox.addLayout(self._buildBuddiesPanel(cWidget))
        vBox.addLayout(self._buildMakeCallPanel(cWidget))
        vBox.addLayout(self._buildButtonsPanel(cWidget))
        
        cWidget.setLayout(vBox)
        self.setCentralWidget(cWidget)
        self.statusBar().showMessage('MostVoip Event Log') # crea una veloce barra di stato
    
    def _buildBuddiesPanel(self, cWidget):
        vBox = QtGui.QVBoxLayout()
        vBox.setSpacing(5)
        self.buddiesList = QtGui.QListView()
        self.buddiesList.setWindowTitle('Buddies')
        #self.buddiesList.setMinimumSize(600, 400)
        
        # Create an empty model for the list's data
        self.buddiesModel = QtGui.QStandardItemModel(self.buddiesList)
        self.buddiesList.setModel(self.buddiesModel)
        self.buddiesList.show()
        vBox.addWidget(QtGui.QLabel('Buddies', cWidget))
        vBox.addWidget(self.buddiesList);
        return vBox
    
    def _buildStatesPanel(self,cWidget):
        hBox1 = QtGui.QHBoxLayout()
        hBox1.setSpacing(5)
        labServerState = QtGui.QLabel('Server State', cWidget)
        self.labServerStateInfo =  QtGui.QLabel('N.A', cWidget)
        hBox1.addWidget(labServerState)
        hBox1.addWidget(self.labServerStateInfo)
        
        hBox2 = QtGui.QHBoxLayout()
        hBox2.setSpacing(5)
        labCallState = QtGui.QLabel('Call State', cWidget)
        self.labCallStateInfo =  QtGui.QLabel('N.A', cWidget)
        hBox2.addWidget(labCallState)
        hBox2.addWidget(self.labCallStateInfo)
        
        hBox3 = QtGui.QHBoxLayout()
        hBox3.setSpacing(5)
        labAccountState = QtGui.QLabel('Account State', cWidget)
        self.labAccountStateInfo =  QtGui.QLabel('N.A', cWidget)
        hBox3.addWidget(labAccountState)
        hBox3.addWidget(self.labAccountStateInfo)
        
        
        vBox = QtGui.QVBoxLayout()
        vBox.setSpacing(5)
        
        vBox.addLayout(hBox1)
        vBox.addLayout(hBox2)
        vBox.addLayout(hBox3)
        return vBox
    
    def _buildMakeCallPanel(self,cWidget):
        hBox = QtGui.QHBoxLayout()
        hBox.setSpacing(2)
        self.butMakeCall = QtGui.QPushButton('Make Call', cWidget)
        self.txtExtension = QtGui.QLineEdit(cWidget)
        hBox.addWidget(self.txtExtension)
        hBox.addWidget(self.butMakeCall)
        return hBox
        
    def _buildButtonsPanel(self,cWidget):
        hBox = QtGui.QHBoxLayout()
        hBox.setSpacing(2)
        self.butInit = QtGui.QPushButton('Init', cWidget)
        self.butAnswer = QtGui.QPushButton('Answer', cWidget)
        
        self.butHold = QtGui.QPushButton('Hold', cWidget)
        self.butHold.setCheckable(True);
        
        self.butHangup = QtGui.QPushButton('Hangup', cWidget)
        hBox.addWidget(self.butInit)
        hBox.addWidget(self.butAnswer)
        hBox.addWidget(self.butHold)
        hBox.addWidget(self.butHangup)
        
        self.connect(self.butInit, QtCore.SIGNAL('clicked()'), self.init_voip_lib);
        return hBox
         
 


app = QtGui.QApplication(sys.argv)
main = MostVoipGUI()
main.show()
sys.exit(app.exec_())

