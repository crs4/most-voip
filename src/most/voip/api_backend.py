'''
Created on 05/mag/2014

@author: smonni
'''

import pjsua as pj
import logging
import sys, os , time
from most.voip.states import VoipState



##########   GLOBAL VARS  ####################

is_server_on = False

buddy = None
is_buddy_on_line = False
is_buddy_on_hold = False

transport = None
input_volume = 0.5
output_volume = 0.5

auto_answer = False
auto_answer_delay = 3

acc = None

current_call = None

player_in_id = None
player_out_id = None

input_volume = 0.5
output_volume = 0.5

remote_root_dir = os.path.dirname(os.path.abspath(sys.argv[0]))

local_hungup = False


##############################################

 
    
   
class VoipBackendCallState:
    NONE = 0  # there isn't any call running, incoming or outcoming
    INCOMING = 1  # a new call is incoming
    OUTCOMING = 2 # a new call is outcoming
    BUSY = 3 # there is a current call
    LOCAL_HOLD = 4 # the local user put the call on hold
    ON_LINE = 5 # call is on line


callState = VoipBackendCallState.NONE

# Logging callback
def log_cb(level, msg, msg_len):
    print '[%s] %s' % (time.ctime(), msg),

def _start_call_sound_out():
    global player_out_id,input_volume, output_volume, remote_root_dir
    #if player_out_id==None:

    sound_file = os.path.join(remote_root_dir, TecapConfig().getConfig().get("VoipBackend","out_call_ring_tone"))
    print "Loading sound file:%s  " % sound_file
    player_out_id = pj.Lib.instance().create_player(sound_file,loop=True)
    print "SETTING CALL IN VOLUME to %s,%s" % (input_volume,output_volume )
    #pj.Lib.instance().conf_set_rx_level(player_out_id,output_volume)
    pj.Lib.instance().conf_set_tx_level(player_out_id,output_volume)
    print "CREATED PLAYER HAVING ID:%s" % player_out_id

    #else:
    #    print 'player already exists'


    print 'try to connect player: %s' % pj.Lib.instance().player_get_slot(player_out_id)
    print 'try to connect to slot: %s' % current_call.info().conf_slot

    pj.Lib.instance().conf_connect(pj.Lib.instance().player_get_slot(player_out_id), 0)


def _stop_call_sound():
    global player_out_id, player_in_id
    print "in stop call sound.. "
    print "player_out_id:%s" % player_out_id
    #print "player_in_id:%s" % player_in_id

    if player_out_id!=None:
        _stop_call_sound_out()
    if player_in_id!=None:
        _stop_call_sound_in()


def _stop_call_sound_out():
    global player_out_id
    print "in _stop_call_sound_out. PLAYER_OUT_ID:%s" % player_out_id
    if player_out_id!=None:
        print "disconnecting player CALL OUT"
        pj.Lib.instance().player_set_pos(player_out_id,0)
        pj.Lib.instance().conf_disconnect(pj.Lib.instance().player_get_slot(player_out_id), 0)
        #pj.Lib.instance().player_destroy(player_out_id)


def _start_call_sound_in():
    global player_in_id, remote_root_dir
    print "in _stop_call_sound_in"
    if player_in_id==None:
        sound_file = os.path.join(remote_root_dir,TecapConfig().getConfig().get("VoipBackend","in_call_ring_tone"))
        player_in_id = pj.Lib.instance().create_player(sound_file,loop=True)
        print "SETTING CALL IN VOLUME to %s,%s" % (input_volume,output_volume )
        pj.Lib.instance().conf_set_tx_level(player_in_id,output_volume)
        #pj.Lib.instance().conf_set_rx_level(player_out_id,input_volume)
        print "CREATED PLAYER HAVING ID:%s" % player_in_id

    pj.Lib.instance().conf_connect(pj.Lib.instance().player_get_slot(player_in_id), 0)


def _stop_call_sound_in():
    global player_in_id
    print "in _stop_call_sound_in"
    if player_in_id!=None:
        print "disconnecting player CALL IN"
        pj.Lib.instance().player_set_pos(player_in_id,0)
        pj.Lib.instance().conf_disconnect(pj.Lib.instance().player_get_slot(player_in_id), 0)
        #pj.Lib.instance().player_destroy(player_in_id)
        #player_in_id = None
 

class VoipBackend:
    
    def _setup_logger(self):
        
        self.logger = logging.getLogger('Voip')
        handler = logging.StreamHandler()
        rootFormatter = logging.Formatter('%(name)s - %(levelname)s: %(msg)s')
        handler.setFormatter(rootFormatter)
        self.logger.addHandler(handler)
        self.logger.setLevel(logging.DEBUG)
        
    def __init__(self):
        
        self._setup_logger()

        global is_server_on, is_buddy_on_line, buddy_status_text
        self.lib = None
        
        is_server_on = False
        is_buddy_on_line = False
        buddy_status_text = 'Unavailable'
        #self.sip_server = TecapConfig().getConfig().get('VoipBackend','sip_server')
        #self.turn_server = TecapConfig().getConfig().get('VoipBackend','turn_server')
        self.lib = None

  

        # indici relativi al dispositivo audio correntemente selezionato nella ListView delle preferences
        self.input_dev_list_index = -1
        self.output_dev_list_index = -1



# Callback to receive events from Call
    class MyCallCallback(pj.CallCallback):


        def __init__(self, notification_cb,call=None):
            pj.CallCallback.__init__(self, call)
            
            self.logger.debug("Istanziata MyCallCallback")
            self.notification_cb = notification_cb

        def on_replace_request(self, code, reason):
            self.logger.debug("richiamata ON REPLACE REQUEST:%s , %s" % (code,reason))
            return (code,reason)


        # Notification when call state has changed
        def on_state(self):
            global current_call, callState, local_hungup, is_buddy_on_line

            
            print "STATO DELLA CALL (on_state):%s" %  self.call.info().state



            uri_to_call = self.call.info().remote_uri
            print "Call with", uri_to_call,
            print "is", self.call.info().state_text,
            print "last code =", self.call.info().last_code,
            print "(" + self.call.info().last_reason + ")"

            if self.call.info().state == pj.CallState.DISCONNECTED:
                current_call = None
                callState = VoipBackendCallState.NONE

                print "DISCONNECTION:Stopping call sound"
                _stop_call_sound()

                print 'Current call is', current_call
                
                if local_hungup:
                    self.logger.debug('Change internal state on HUNGUPPING from on_state dopo HUNGUP')
                    self.notification_cb(VoipState.Hungup, {'success': True, 'call_state' :callState})
                    local_hungup = False
                else:
                    if is_buddy_on_line:
                        print 'Change internal state on HUNGUPPING from on_state  dopo REMOTE HUNGUP'
                        self.notification_cb(VoipState.RemoteHungup, {'success': True, 'call_state' :callState})
                    else:
                        self.logger.debug('Change internal state on HUNGUPPING from on_state  dopo REMOTE DISCONNECTION HUNGUP')
                        self.notification_cb(VoipState.RemoteDisconnectionHungup, {'success': True, 'call_state' :callState})
                        #self.sip_controller.change_state(SipControllerState.RemoteDisconnectionHungup,callState)



            elif self.call.info().state==pj.CallState.CONFIRMED:
                print "CALL CONFIRMED" #. sending REQUEST to %s" % uri_to_call
                self.logger.debug('Change internal state on CALLING')
                #self.sip_controller.change_state(SipControllerState.Calling, callState)
                self.notification_cb(VoipState.Calling, {'success': True, 'call_state' :callState})
                callState = VoipBackendCallState.BUSY





        # Notification when call's media state has changed.
        def on_media_state(self):
            
            global callState, input_volume, output_volume
            print 'DENTRO ON MEDIA STATE:%s' % self.call.info().media_state
            if self.call.info().media_state == pj.MediaState.ACTIVE:
                print "Stopping ring tone...."
                _stop_call_sound()
                # Connect the call to sound device
                call_slot = self.call.info().conf_slot
                pj.Lib.instance().conf_connect(call_slot, 0)
                pj.Lib.instance().conf_connect(0, call_slot)
                
                print "VOLUME INIZIALE:" + str(pj.Lib.instance().conf_get_signal_level(call_slot))
                pj.Lib.instance().conf_set_rx_level(call_slot,input_volume)
                pj.Lib.instance().conf_set_tx_level(call_slot,output_volume)


                print "Media is now active"
                #self.messenger.send_info("Call online")
                #self.messenger.update_call_button_label("Hungup")
                callState = VoipBackendCallState.BUSY


                #uri_to_call = self.call.info().remote_uri
                #print "CALL CONFIRMED. sending REQUEST to::: %s" % uri_to_call
                #self.call.send_pager(uri_to_call, "messaggio con la uri!!!", im_id="12345", content_type='text/plain')  #, hdr_list=["user=admin","secret=secret5"])
            elif self.call.info().media_state == pj.MediaState.LOCAL_HOLD:
                print 'Local Hold request'
                callState = VoipBackendCallState.LOCAL_HOLD
                #self.sip_controller.change_state(SipControllerState.Holding, callState)
                self.notification_cb(VoipState.RemoteDisconnectionHungup, {'success': True, 'call_state' :callState})

            elif self.call.info().media_state == pj.MediaState.REMOTE_HOLD:
                print "Media is REMOTE HOLD STATE"
            else:
                print "Media is inactive"

                #self.messenger.send_info("No Call")
                #self.messenger.update_call_button_label("Call")
                
                callState = VoipBackendCallState.NONE
                



    class MyBuddyCallback(pj.BuddyCallback):
        def __init__(self, notification_cb, buddy=None):
            pj.BuddyCallback.__init__(self, buddy)
            self.notification_cb = notification_cb

        def on_state(self):
            print "Buddy", self.buddy.info().uri, "is --> ",
            print self.buddy.info().online_status, " ->",
            print self.buddy.info().online_text + " <--"

            global is_buddy_on_line, is_buddy_on_hold, buddy_status_text, callState
            is_buddy_on_line = self.buddy.info().online_status==1 and self.buddy.info().online_text != 'On hold'
            is_buddy_on_hold = self.buddy.info().online_status==1 and self.buddy.info().online_text == 'On hold'
            is_buddy_off_line = self.buddy.info().online_status==2
            buddy_status_text = self.buddy.info().online_text

            if is_buddy_on_line:
                self.logger.debug('mando change state di Buddy CONNECTED')
                #self.sip_controller.change_state(SipControllerState.Remote_user_connected, buddy_status_text)
                self.notification_cb(VoipState.Remote_user_connected, {'buddy_status' : buddy_status_text})
            elif is_buddy_on_hold:
                print 'Buddy in REMOTE HOLD!!!'
                if callState == VoipBackendCallState.LOCAL_HOLD:
                    #self.sip_controller.change_state(SipControllerState.RemoteLocalHolding,buddy_status_text)
                    self.notification_cb(VoipState.RemoteLocalHolding, {'buddy_status' : buddy_status_text})
                    pass
                else:
                    #self.sip_controller.change_state(SipControllerState.RemoteHolding,buddy_status_text)
                    self.notification_cb(VoipState.RemoteHolding, {'buddy_status' : buddy_status_text})
                    pass
            elif is_buddy_off_line:
                self.logger.debug('mando change state di Buddy DISCONNECTED')
                #self.sip_controller.change_state(SipControllerState.Remote_user_disconnected, buddy_status_text)
                self.notification_cb(VoipState.Remote_user_disconnected, {'buddy_status' : buddy_status_text})


            #self.messenger.update_status_label(SignalEmitterState.NO_CALL)

        def on_pager(self, mime_type,body):
            print "Instant message in BuddyCallBack from", self.buddy.info().uri,
            print "(", mime_type, "):"
            print body

        def on_pager_status(self, body, im_id, code, reason):
            if code >= 300:
                print "Message delivery failed for message",
                print body, "to", self.buddy.info().uri, ":", reason

        def on_typing(self, is_typing):
            if is_typing:
                print self.buddy.info().uri, "is typing"
            else:
                print self.buddy.info().uri, "stops typing"


 
    
    # Callback to receive events from account
    class MyAccountCallback(pj.AccountCallback):

        def __init__(self, notification_cb, account=None):
            pj.AccountCallback.__init__(self, account)
            self.notification_cb = notification_cb
            self.already_registered = False
            self.auto_answer_call = None

            self.REQUEST_TIMEOUT = 408
            self.FORBIDDEN = 403
            self.NOT_FOUND = 404
            self.REGISTERED = 200
            self.SERVICE_UNAVAILABLE = 503


        """
        def on_incoming_subscribe(self, my_buddy, from_uri, contact_uri, pres_obj):
            print '\n\nrichiamato on subscribe del buddy:%s da %s a %s ' % (my_buddy, from_uri, contact_uri)

            return (200,None)
        """
        
        # Notification on local user registration change state (a request timeout status code implies that the sip server is disconnected)
        def on_reg_state(self):
            print '\ncalled on reg status:%s' % self.account.info().reg_status
            print 'called on reg reason:%s\n' % self.account.info().reg_reason
            print 'on line text:%s' % self.account.info().online_text
            
            global is_server_on
            # utente registrato con successo (evidentemente il server e' su
            reg_status = self.account.info().reg_status
            reg_reason = self.account.info().reg_reason

            is_server_on_new = reg_status!=self.REQUEST_TIMEOUT
            if is_server_on_new!=is_server_on:
                is_server_on = is_server_on_new

            if reg_status in [self.REQUEST_TIMEOUT, self.SERVICE_UNAVAILABLE]:
                #self.sip_controller.change_state(SipControllerState.Connection_failed, {'reg_status': reg_status, 'reg_reason': reg_reason})
                #self.sip_controller.do_fsm(SipControllerState.Connection_failed,{'reg_status': reg_status, 'reg_reason': reg_reason})
                self.notification_cb(VoipState.Connection_failed, {'Success' : False, 'reg_status': reg_status, 'reg_reason': reg_reason})
                self.already_registered = False
                
            elif reg_status==self.REGISTERED:
                if not self.already_registered:
                    self.logger.debug("LOCAL USER REGISTERED")
                    #self.sip_controller.change_state(SipControllerState.Registered,self.account.info())
                    #self.sip_controller.do_fsm(SipControllerState.Registered,self.account.info())
                    self.notification_cb(VoipState.Registered, {'Success' : True, 'account_info': self.account.info()})
                    self.already_registered = True
            else:
                print 'LOCAL USER REGISTRATION FAILED:%s, %s' % (reg_status,reg_reason)
                #self.sip_controller.change_state(SipControllerState.Registration_failed, {'reg_status': reg_status, 'reg_reason': reg_reason})
                #self.sip_controller.do_fsm(SipControllerState.Registration_failed,{'reg_status': reg_status, 'reg_reason': reg_reason})
                self.notification_cb(VoipState.Registration_failed, {'Success' : False, 'reg_status': reg_status, 'reg_reason': reg_reason})

        # Notification on incoming call
        def on_incoming_call(self, call):
            print '\nINCOMING CALL\n'
            global current_call,config, callState, refused, auto_answer, auto_answer_delay
            refused = False

            if current_call:
                callState = VoipBackendCallState.BUSY
                print 'Chiamata occupata'
                call.answer(486, "Busy")
                return
            ru = str(call.info().remote_uri)
            remote_contact = ru[ru.index('"')+1:ru.rindex('"')]
            print "Incoming call from ", call.info().remote_uri

        
            callState = VoipBackendCallState.INCOMING
            #_start_call_sound(config.get("VoipBackend","in_call_ring_tone"))
            #_start_call_sound_in()
            current_call = call
            call_cb = VoipBackend.MyCallCallback(self.notification_cb,current_call)
            current_call.set_callback(call_cb)

            print 'cambio lo stato interno del sip controller in dialing!'
            #self.sip_controller.change_state(SipControllerState.Dialing, callState)
            self.notification_cb(VoipState.Dialing, {'Success' : True, 'call_state': callState})
            if auto_answer==True:


                print "auto answering after %s seconds" % auto_answer_delay

                #self.messenger.send_info("Auto Answering to Incoming call from %s in %s seconds" % (remote_contact,delay))
                #self.messenger.update_status_label("Auto Answering to Incoming call from %s in %s seconds" % (remote_contact,delay))
                #self.messenger.update_status_label(SignalEmitterState.AUTO_ANSWERING_CALL)
                
                #TODO: sostituire il time sleep con il timer.add

                current_call.answer(180)
                self.auto_answer_call = self.sip_controller.loop.timer_add(auto_answer_delay*1000,self._auto_answer) ## TODO
            else:
                current_call.answer(180)

        def _auto_answer(self):
            global current_call, callState
            print 'RISPOSTA AUTOMATICA!'
            self.auto_answer_call = None
             
            if callState==VoipBackendCallState.INCOMING:
                current_call.answer(200)


    def _initialize_values(self):
        print 'initializing audio values temporary disabled'
        """
        global input_volume, output_volume, auto_answer, auto_answer_delay
        config = TecapConfig().getConfig()

        input_volume = config.getfloat('VoipBackend','input_volume')
        output_volume = config.getfloat('VoipBackend','output_volume')
        auto_answer = config.getboolean('VoipBackend','auto_answer')
        auto_answer_delay = config.getint('VoipBackend','auto_answer_delay')

        #self.sip_controller.view.control_panel.mic_volume.slider.setValue(input_volume*100)
        #self.sip_controller.view.control_panel.out_volume.slider.setValue(output_volume*100)
        print "reading audio I/O devices..."
        in_device = config.getint('VoipBackend','in_device')
        out_device = config.getint('VoipBackend','out_device')
        sel_in = config.getint('VoipBackend','in_sel_device')
        sel_out = config.getint('VoipBackend','out_sel_device')

        self.set_audio_devices(in_device,out_device,sel_in,sel_out)
        """

    def get_inout_devices(self):
        devices = self.lib.enum_snd_dev()
        num_devices = len(devices)
        in_devices = []
        out_devices = []
        for i in range(num_devices):
            if devices[i].input_channels>0:
                in_devices.append((devices[i],i))
            if devices[i].output_channels>0:
                out_devices.append((devices[i],i))
        return (in_devices,out_devices)


    def set_audio_devices(self, input_device, output_device, list_index_in, list_index_out):
        print 'dentro set_audio_devices di audio_chat'
        try:
            self.lib.set_snd_dev(input_device, output_device)
            self.input_dev_list_index =  list_index_in
            self.output_dev_list_index =  list_index_out
        except Exception , ex:
            print "Eccezione:%s" % str(ex)

    def set_call_preferences(self, auto_answering, auto_answering_delay):
        print 'dentro set_call_preferences di audio_chat'
        global auto_answer, auto_answer_delay
        try:
            auto_answer = auto_answering
            auto_answer_delay = auto_answering_delay
        except Exception , ex:
            print "Eccezione:%s" % str(ex)


    def get_audio_devices(self):
        return (self.lib.get_snd_dev(), self.input_dev_list_index, self.output_dev_list_index)
    
    def send_inout_devices_info(self):
        inout_devices = self.get_inout_devices()
        sel_io_devices = self.get_audio_devices()
        global auto_answer, auto_answer_delay
        params = { 'selected_devices' : sel_io_devices,
                   'input_devices' : inout_devices[0],
                   'output_devices' : inout_devices[1],
                   'auto_answer' : auto_answer,
                   'auto_answer_delay' : auto_answer_delay
                  }

        #self.sip_controller.request_main_state_change(RequestStateChange.Sending_AudioDevicesList,params) # TODO

    def is_server_on(self):
        global is_server_on 
        return is_server_on

    def getInputVolume(self):
        global input_volume
        return input_volume

    def setInputVolume(self, newValue):
        global input_volume
        input_volume = newValue/100.0
        #print "input volume setted to:%s" % input_volume
        global current_call
        if current_call:
            print "Mi cerco lo slot"
            slot = current_call.info().conf_slot
            print "lo slot:" + str(slot)
            if slot!=None and slot>-1:
                pj.Lib.instance().conf_set_tx_level(slot,input_volume)
                #pj.Lib.instance().conf_set_rx_level(slot,input_volume)
            else:
                print "SLOT NULLO!"

    def getOutputVolume(self):
        global output_volume
        return output_volume

    def setOutputVolume(self, newValue):
        global output_volume
        output_volume = newValue/100.0
        #print "output volume setted to:%s" % output_volume
        global current_call, player_out_id, player_in_id

        if player_out_id!=None and player_out_id>-1:
            #pj.Lib.instance().conf_set_rx_level(player_out_id,output_volume)
            pj.Lib.instance().conf_set_tx_level(player_out_id,output_volume)
            #pj.Lib.instance().conf_set_tx_level(player_out_id,input_volume)
        if player_in_id!=None and player_in_id>-1:
            #pj.Lib.instance().conf_set_rx_level(player_in_id,output_volume)
            pj.Lib.instance().conf_set_tx_level(player_in_id,output_volume)

        if current_call:
            slot = current_call.info().conf_slot
            print "lo slot:" + str(slot)
            if slot!=None and slot>-1:
                pj.Lib.instance().conf_set_rx_level(slot, output_volume)
            else:
                print "SLOT NULLO!"
    
    def unregister_account(self):
        
        if self.is_server_on():

            self.logger.debug("Unregistering account having active? %s is_valid? %s" % (acc.info().reg_active, acc.is_valid()))
            try:
                if acc.info().reg_active:
                    acc.set_registration(False)
                    #acc.delete()
                    #acc = None
                    return True
            except Exception,ex:
                self.logger.exception("Unexpected exception during user unregistration, maybe because the sip server is offline:%s" % ex)
                return False

            
    
    def finalize(self):
        if not self.lib:
            self.logger.warn('No Pjsip lib to shutdown')
            return False
        
        print 'PJSIP SHUTTING DOWN....'
        global acc
        global transport
        global current_call
        global buddy
        global player_out_id
        global player_in_id


        # _stop_call_sound()

        # if player_out_id:
        #     pj.Lib.instance().player_destroy(player_out_id)
        # if player_in_id:
        #     pj.Lib.instance().player_destroy(player_in_id)
        
        if player_out_id:
            print 'deleting out id'
            del player_out_id
            player_out_id = None
        if player_in_id:
            print 'deleting in id'
            del player_in_id
            player_in_id = None


        # lo shutdown della libreria non e' consentito se c'e' una call in corso
#        if current_call:
#            self.hungup_call()
#            while self.is_current_call():
#                time.sleep(1)
         
        self.unregister_account()
        
        try:
            if buddy!=None:
                buddy.unsubscribe()
                buddy.delete()
                del buddy
                buddy = None
                
            if acc!=None:
                acc.delete()
                del acc
                acc = None


            # Shutdown the library
            transport.close(True)
            print "After force transport close"
            transport = None
            print "destroying pjsip lib"
            self.lib.destroy()
            
            print "library destroyed"
            del self.lib
            self.lib = None
        except Exception, e:
            print 'Exception during shutting down:%s' % e
            return False

        return True
    
    

    def register_account(self):
        try:
            global acc

            if not acc:

                self.logger.debug("Registering account **%s** (PWD:%s) on sip server:**%s**" % (self.my_account[0],self.my_account[1], self.sip_server))
                acc_cfg = pj.AccountConfig('%s;transport=tcp' % str(self.sip_server), str(self.my_account[0]), str(self.my_account[1]))
                #acc_cfg = pj.AccountConfig('156.148.132.244', 'demo-smonni', 'pwd_smonni')

               
                # la riregistrazione avviene ogni 60 secondi che e' il minimo consentito (verifica la presenza del server)
                acc_cfg.reg_timeout = 60
                acc_cfg.publish_enabled = True
                

                acc = self.lib.create_account(acc_cfg, cb=VoipBackend.MyAccountCallback(None)) #TODO inserire oggetto che riceve le notifiche di cambio di stato

                self.logger.debug("Account %s registration successfully sent with timeout:%s" % (self.my_account[0], acc_cfg.reg_timeout))
                #self.sip_controller.change_state(SipControllerState.Registered, self.my_account[0])
                #self.sip_controller.do_fsm(SipControllerState.Registered,self.my_account[0])
                self.notification_cb(VoipState.Registered, {'Success' : True, 'account_info' : self.my_account[0]})
            else:
                self.logger.debug("account previously registered. Nothing to do")
            
            return True

            
        except pj.Error, e:
            self.logger.exception("Exception registering account:%s" % str(e))
            self.lib.destroy()
            self.lib = None
            #self.sip_controller.change_state(SipControllerState.Registration_failed, str(e))
            #self.sip_controller.do_fsm(SipControllerState.Registration_failed,self.params)
            self.notification_cb(VoipState.Registration_failed, {'Success' : False, 'error' : str(e), 'params': self.params})
            return False


    def initialize(self,params, notification_cb):

        # Create library instance
        # Create pjsua before anything else
        self.logger.debug("\nSTARTING SIP...%s\n" % str(params))
        if not self.lib:
            self.lib = pj.Lib()
        
  
        self.params = params
        self.notification_cb = notification_cb
        
        self.sip_server = str(self.params['sip_server'])
        self.turn_server = str(self.params['turn_server'])
        print 'reading server: %s:%s' % (self.sip_server, self.turn_server)
        self.my_account = (self.params['sip_user'], self.params['sip_pwd'])
        self.logger.debug("ACCOUNT FROM WEB APP:%s,%s" % self.my_account)



        try:
            # Init library with default config and some customized

            my_media_cfg = pj.MediaConfig()
            my_media_cfg.enable_ice = True
            my_media_cfg.enable_turn = True
            my_media_cfg.turn_server = "%s:3478" % str(self.turn_server)
            #my_media_cfg.turn_server = "156.148.18.186:3478"
            self.logger.debug("Setting turn server[%s]:%s" % (type(my_media_cfg.turn_server), (my_media_cfg.turn_server)))
            my_media_cfg.turn_cred = pj.AuthCred("remote.most.it", '%s' % str(self.params['turn_user']), '%s' % str(self.params['turn_pwd']))
            self.logger.debug("#%s#" % my_media_cfg.turn_cred)
            my_media_cfg.turn_conn_type = pj.TURNConnType.TCP
            self.logger.debug("Setting turn user:%s:%s" % (self.params['turn_user'], self.params['turn_pwd']))

            #my_media_cfg.snd_play_latency = 0
            #my_media_cfg.snd_rec_latency = 0
            my_media_cfg.jb_max = 1000

            my_media_cfg.no_vad = True
            my_media_cfg.quality = 10
            
            #my_media_cfg.snd_clock_rate = 22050
            #my_media_cfg.ptime = 20
            LOG_LEVEL =  self.params["log_level"]

            ua_cfg = pj.UAConfig()
            #ua_cfg.stun_host = "216.93.246.14"
            #ua_cfg.stun_host = "156.148.18.187"
            #ua_cfg.stun_host = "stun.xten.com"
            #ua_cfg.stun_host = "stun.voipbuster.com"
            #ua_cfg.stun_host = "216.93.246.14"
            
            self.lib.init(log_cfg = pj.LogConfig(level=LOG_LEVEL, callback=log_cb),  media_cfg=my_media_cfg, ua_cfg=ua_cfg)

            print "looking for installed devices..."
            #self.lib.set_snd_dev(0,0)
            devices = self.lib.enum_snd_dev()
            print "Devices found:%s" % len(devices)
            snd_dev = self.lib.get_snd_dev()
            
            print "My Sound devs:" + str(snd_dev) ## returns (-1,-2)
            #assert len(devices)>0
            
            
            for d in devices:
                print "DEVICE:" + d.name + " > sr:" + str(d.default_clock_rate)



            #print "creating UDP transport..."
            global transport
            #transport = self.lib.create_transport(pj.TransportType.UDP)
            transport = self.lib.create_transport(pj.TransportType.TCP)
            self.logger.debug("Creating transport:%s" % transport)

            # Start the library
            self.lib.start(with_thread=True)

 
            c = self.lib.enum_codecs()
            self.logger.debug("List of Codecs(%s)" % len(c))
            for codec in c:
                self.logger.debug("Found codec %s with priority: %s" % (codec.name, codec.priority))
                if not codec.name.startswith('iLBC'):pass
                    #self.lib.set_codec_priority(codec.name, 0)
                    #print 'Setting codec priority to 0 for %s' % codec.name
                    #codec.priority=0
 
            self._initialize_values()
            self.notification_cb(VoipState.Initialized, {'success': True, 'sip_server' :self.sip_server})
            #self.sip_controller.change_state(SipControllerState.Initialized, self.sip_server)
            #self.sip_controller.do_fsm(SipControllerState.Initialized,self.params)

            self.logger.debug('SIP successfully initialized!')
            return True
        except pj.Error, e:
            self.logger.debug( "SIP INITIALIZATION FAILED: Exception: " + str(e))
            self.lib.destroy()
            self.lib = None
            return False
        
            #self.sip_controller.change_state(SipControllerState.Initialize_failed, str(e))
            self.notification_cb(VoipState.Initialize_failed, {'success': False, 'error' :str(e)})
            #self.sip_controller.do_fsm(SipControllerState.Initialize_failed,self.params)


    def add_buddy(self, dest_extension):
        print '\nADDING REMOTE USER...\n'
        global buddy
        try:
            global acc
            dest_uri = "sip:%s@%s;transport=tcp" % (str(dest_extension), self.sip_server)
            
            print 'adding buddy:%s' % dest_uri
            buddy = acc.add_buddy(dest_uri,cb=VoipBackend.MyBuddyCallback(self.notification_cb))
            buddy.subscribe()
            print 'REMOTE USER ADDED'
            #self.sip_controller.do_fsm(SipControllerState.Remote_user_subscribed,dest_extension)
            self.notification_cb(VoipState.Remote_user_subscribed, {'success': True, 'dest_extension' : dest_extension})
            #self.sip_controller.change_state(SipControllerState.Remote_user_subscribed, dest_extension)
        except pj.Error, e:
            print "ADDING REMOTE USER FAILED: Exception: " + str(e)
            #self.sip_controller.do_fsm(SipControllerState.Remote_user_subscribing_failed,dest_extension)
            self.notification_cb(VoipState.Remote_user_subscribing_failed, {'success': False, 'dest_extension': dest_extension, 'error' :str(e)})
            #self.sip_controller.change_state(SipControllerState.Remote_user_subscribing_failed, dest_extension)



    def is_buddy_on_line(self):
        global is_buddy_on_line
        return is_buddy_on_line

    def is_buddy_on_hold(self):
        global is_buddy_on_hold
        return is_buddy_on_hold

    def get_buddy_status_text(self):
        global buddy_status_text
        return buddy_status_text


    # Function to make call
    def make_call(self,dest_extension):

        global current_call, config
        global acc, callState
        if not dest_extension:
            print "No sip address provided. Call canceled"
            return
        
        uri = 'sip:%s@%s' % (str(dest_extension), str(self.sip_server))

        print "MAKE CALL:%s" % str(current_call)
        print "STATO DELLA CALL (make_call): %s " % str (callState)
        try:
            """
            if current_call:
                if callState == VoipBackendCallState.INCOMING: # ringing
                    print "IN_COMING call so ANSWERING..."
                    self.answer_call()
                    return
                else:
                    print "OUT_COMING call so NO ANSWERING..."
                    return
            """



            print "Making call to:%s" % uri
            #self.messenger.send_info("Dialing to %s (%s)" % (contact, uri))
            #self.messenger.update_status_label("Dialing call to %s" % uri)
            #self.messenger.update_status_label(SignalEmitterState.OUTCOMING_CALL)
            lck = self.lib.auto_lock()

            print "CURRENT CALL PRIMA:%s" % current_call


            callState = VoipBackendCallState.OUTCOMING
            current_call = acc.make_call(uri, cb=VoipBackend.MyCallCallback(self.notification_cb)) # todo inserire Listener
            _start_call_sound_out()
            #self.messenger.update_call_button_label("Hungup")
            
            print "CURRENT CALL DOPO:%s" % current_call
            del lck
            print 'Current call is', current_call

        except pj.Error, e:
            print ">>>> Exception in make_call: " + str(e)

    def answer_call(self):
        global current_call

        try:
            if not current_call:
                print 'There is no call'
                return
            elif current_call.info().state!=pj.CallState.CONFIRMED:
                _stop_call_sound()
                current_call.answer(200)
                print 'Answer'
            else:
                print 'Call On line: answer call ignored'
        except Exception, ex:
            print 'ECCEZIONE in answer call:%s' % ex
            
    def hungup_call(self):
        #global refused
        #refused = True
        print "RICHIESTA HUNGUP"
        try:
            _stop_call_sound()

            global current_call, local_hungup

            if current_call:
                print "Hungup Call (current_call!=null)."
                local_hungup = True
                current_call.hangup()
                #self.sip_controller.change_state(SipControllerState.Hungup,'')
                self.notification_cb(VoipState.Hungup, {'success': True })
                current_call = None
                return True


            else:
                self.logger.debug("There is no call to hungup")
                #self.messenger.send_info("No Call to hungup!")
                return False

        except Exception,ex:
            self.logger.exception("Exception in hungup call:%s" % ex) 
            return False

    def hold_call(self):
        try:
            global current_call
            if current_call:
                print 'holding current call...'
                current_call.hold()
                if self.is_buddy_on_hold():
                    #self.sip_controller.change_state(SipControllerState.RemoteLocalHolding,'')
                    self.notification_cb(VoipState.RemoteLocalHolding, {'success': True})
                    self.logger.debug("remote local holding!")
                else:
                    #self.sip_controller.change_state(SipControllerState.Holding,'')
                    self.notification_cb(VoipState.Holding, {'success': True})
                    self.logger.debug("remote holding!")
                return True
            else:
                print "There is no call to hold"
                #self.messenger.send_info("No Call to hungup!")
                return False
        except Exception,ex:
            self.logger.exception("Exception in hold_call:%s" % ex)
            return False

    def unhold_call(self):
        try:
            global current_call
            if current_call:
                current_call.unhold()
                if self.is_buddy_on_hold():
                    #self.sip_controller.change_state(SipControllerState.RemoteHolding,'') ### TODO : check if this is correct or not
                    self.notification_cb(VoipState.RemoteHolding, {'success': True}) ## REMOTE BUG?!?!
                    self.logger.debug( "remote unholding!")
                else:   
                    #self.sip_controller.change_state(SipControllerState.Calling,'')
                    self.notification_cb(VoipState.Calling, {'success': True})
                    print "remote local unholding!"
                return True
            else:
                print "There is no call to unhold"
                #self.messenger.send_info("No Call to hungup!")
                return False
        except Exception,ex:
            print "ECCEZIONE in unhold_call:%s" % ex
            return False



    def serialize_values(self):
        if not self.lib:
            print 'PJSIP Library no yet initialized. Nothing to serialize!!'
            return
        
        print 'serializing audio preferences'

        global input_volume, output_volume, auto_answer, auto_answer_delay
        
        config = TecapConfig().getConfig()
        config.set('VoipBackend','input_volume', str(input_volume))
        config.set('VoipBackend','output_volume', str(output_volume))

        current_devices = self.get_audio_devices()
        config.set('VoipBackend','in_device', str(current_devices[0][0]))
        config.set('VoipBackend','out_device', str(current_devices[0][1]))
        config.set('VoipBackend','in_sel_device', str(current_devices[1]))
        config.set('VoipBackend','out_sel_device', str(current_devices[2]))
        config.set('VoipBackend','auto_answer' , str(auto_answer))
        config.set('VoipBackend','auto_answer_delay' , str(auto_answer_delay))
        TecapConfig().write()
        print 'config serialized...'

