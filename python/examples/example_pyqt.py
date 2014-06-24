#!/usr/bin/python

import sys
from PyQt4 import QtGui, QtCore  

from most.voip.api import VoipLib
from most.voip.constants import VoipEvent

class MostVoipGUI(QtGui.QMainWindow):
    
    def notify_events(self, voip_event_type,voip_event, params):
        msg = "%s: %s" % (voip_event_type,voip_event)
        self.statusBar().showMessage(msg)
        
        self._update_status_labels()
        
        if voip_event==VoipEvent.LIB_INITIALIZED:
            self.myVoip.register_account();

            
        
    def __init__(self):
        QtGui.QMainWindow.__init__(self)
        self.setWindowTitle('Most Voip Demo Application')
        self.myVoip = VoipLib()
        
        cWidget = QtGui.QWidget(self)
        
        vBox = QtGui.QVBoxLayout()
        vBox.setSpacing(5)
        vBox.addLayout(self._buildStatesPanel(cWidget))
        vBox.addLayout(self._buildBuddiesPanel(cWidget))
        vBox.addLayout(self._buildButtonsPanel(cWidget))
        
        cWidget.setLayout(vBox)
        self.setCentralWidget(cWidget)
        self.statusBar().showMessage('MostVoip Event Log') # crea una veloce barra di stato
        
        
    
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
        
    def _update_server_state(self):
        server_state = self.myVoip.get_server_state()
        self.labServerStateInfo.setText(server_state)
        
    def _update_call_state(self):
        call_state = self.myVoip.get_call_state()
        self.labCallStateInfo.setText(call_state)


# -----------------------------------------------------------------------------
# GUI BUILDING SECTION
# -----------------------------------------------------------------------------
    def _buildBuddiesPanel(self, cWidget):
        vBox = QtGui.QVBoxLayout()
        vBox.setSpacing(5)
        bubbiesList = QtGui.QListView()
        bubbiesList.setWindowTitle('Buddies')
        bubbiesList.setMinimumSize(600, 400)
        
        # Create an empty model for the list's data
        model = QtGui.QStandardItemModel(bubbiesList)
        vBox.addWidget(QtGui.QLabel('Buddies', cWidget))
        vBox.addWidget(bubbiesList);
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
        
        
        vBox = QtGui.QVBoxLayout()
        vBox.setSpacing(5)
        
        vBox.addLayout(hBox1)
        vBox.addLayout(hBox2)
        return vBox
    
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

