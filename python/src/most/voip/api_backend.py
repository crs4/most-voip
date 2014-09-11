#
# Project MOST - Moving Outcomes to Standard Telemedicine Practice
# http://most.crs4.it/
#
# Copyright 2014, CRS4 srl. (http://www.crs4.it/)
# Dual licensed under the MIT or GPL Version 2 licenses.
# See license-GPLv2.txt or license-MIT.txt
#


import pjsua as pj
import logging
import os , time
from most.voip.interfaces import IServer, ICall, IAccount, IBuddy
from most.voip.constants import VoipEvent, CallState, BuddyState, ServerState, AccountState, VoipEventType


##########   GLOBAL VARS  ####################

logger = None

voip_data_root_dir = "../../data"
is_server_on = False

#buddy = None
#is_buddy_on_line = False
#is_buddy_on_hold = False

buddies_dict = {}

transport = None
input_volume = 0.5
output_volume = 0.5

auto_answer = False
auto_answer_delay = 3

acc = None
account_state = AccountState.UNREGISTERED

current_call = None

player_in_id = None
player_out_id = None

input_volume = 0.5
output_volume = 0.5



auto_answer = False
auto_answer_delay = 3
voip_root_dir = os.path.join(os.path.dirname(__file__), "../../")
in_call_ring_tone = "data/sounds/ring_in_call.wav"   
out_call_ring_tone = "data/sounds/ring_out_call.wav"  


input_volume = 0.5
output_volume = 0.5
in_device = -1
out_device = -2
in_sel_device = -1
out_sel_device = -1
 
local_hungup = False


##############################################

 

callState = CallState.IDLE

# Logging callback
def log_cb(level, msg, msg_len):
    print '[%s] %s' % (time.ctime(), msg),

def _start_call_sound_out():
    global player_out_id,input_volume, output_volume, voip_root_dir
    #if player_out_id==None:
   
    sound_file = os.path.join(voip_root_dir,out_call_ring_tone)
    logger.debug("Loading sound file:%s  " % sound_file)
    player_out_id = pj.Lib.instance().create_player(sound_file,loop=True)
    logger.debug("SETTING CALL IN VOLUME to %s,%s" % (input_volume,output_volume ))
    #pj.Lib.instance().conf_set_rx_level(player_out_id,output_volume)
    pj.Lib.instance().conf_set_tx_level(player_out_id,output_volume)
    logger.debug("CREATED PLAYER HAVING ID:%s" % player_out_id)

    #else:
    #    print 'player already exists'


    logger.debug('try to connect player: %s' % pj.Lib.instance().player_get_slot(player_out_id))
    logger.debug( 'try to connect to slot: %s' % current_call.info().conf_slot)

    pj.Lib.instance().conf_connect(pj.Lib.instance().player_get_slot(player_out_id), 0)


def _stop_call_sound():
    global player_out_id, player_in_id
    logger.debug("in stop call sound.. ")
    logger.debug("player_out_id:%s" % player_out_id)
    #print "player_in_id:%s" % player_in_id

    if player_out_id!=None:
        _stop_call_sound_out()
    if player_in_id!=None:
        _stop_call_sound_in()


def _stop_call_sound_out():
    global player_out_id
    logger.debug("in _stop_call_sound_out. PLAYER_OUT_ID:%s" % player_out_id)
    if player_out_id!=None:
        logger.debug("disconnecting player CALL OUT")
        pj.Lib.instance().player_set_pos(player_out_id,0)
        pj.Lib.instance().conf_disconnect(pj.Lib.instance().player_get_slot(player_out_id), 0)
        #pj.Lib.instance().player_destroy(player_out_id)


def _start_call_sound_in():
    global player_in_id, voip_root_dir
    logger.debug("in _start_call_sound_in")
    if player_in_id==None:
        #sound_file = os.path.join(voip_root_dir,TecapConfig().getConfig().get("VoipBackend","in_call_ring_tone"))
        sound_file = os.path.join(voip_root_dir,in_call_ring_tone)
        player_in_id = pj.Lib.instance().create_player(sound_file,loop=True)
        logger.debug("SETTING CALL IN VOLUME to %s,%s" % (input_volume,output_volume ))
        pj.Lib.instance().conf_set_tx_level(player_in_id,output_volume)
        #pj.Lib.instance().conf_set_rx_level(player_out_id,input_volume)
        logger.debug("CREATED PLAYER HAVING ID:%s" % player_in_id)

    pj.Lib.instance().conf_connect(pj.Lib.instance().player_get_slot(player_in_id), 0)


def _stop_call_sound_in():
    global player_in_id
    logger.debug("in _stop_call_sound_in")
    if player_in_id!=None:
        logger.debug("disconnecting player CALL IN")
        pj.Lib.instance().player_set_pos(player_in_id,0)
        pj.Lib.instance().conf_disconnect(pj.Lib.instance().player_get_slot(player_in_id), 0)
        #pj.Lib.instance().player_destroy(player_in_id)
        #player_in_id = None
 
def _setup_logger():
        global logger
        if not logger:
            logger = logging.getLogger("Voip") #('Voip')
            
            handler = logging.StreamHandler()
    #         rootFormatter = logging.Formatter('%(name)s - %(levelname)s: %(msg)s')
    #         handler.setFormatter(rootFormatter)
            logger.addHandler(handler)
            logger.setLevel(logging.DEBUG)
            #print "NUM LOGGER HANDLERS:%s" % len(logger.handlers)
        
class VoipBackend:
    
    def __init__(self):
        _setup_logger()
        
        global is_server_on #is_buddy_on_line, buddy_status_text
        self.lib = None
        self.sip_server = VoipBackend.SipServer("N.A", ServerState.DISCONNECTED)
        
        is_server_on = False
        self.lib = None
        self.notification_cb = None

  

        # indici relativi al dispositivo audio correntemente selezionato nella ListView delle preferences
        self.input_dev_list_index = -1
        self.output_dev_list_index = -1

      
      
    class SipAccount(IAccount):    
        global account_state, acc
        
        def __init__(self, notification_cb, sip_server):
            self.notification_cb = notification_cb
            self.sip_server = sip_server
        
    
        def get_state(self):
            return account_state 
        
        def get_uri(self):
            if acc==None:
                return None
            else:
                return acc.info().uri
            
            
        def _get_buddy_uri_key(self, extension):
            return "sip:%s@%s;transport=tcp" % (str(extension), self.sip_server)
    
        def add_buddy(self, buddy_extension):
            global buddies_dict
            logger.debug( '\nADDING REMOTE USER with extension:%s\n' % buddy_extension)
            try:
                global acc
                dest_uri = self._get_buddy_uri_key(buddy_extension)
                
                logger.debug( 'adding buddy:%s' % dest_uri)
                buddy = acc.add_buddy(dest_uri,cb=VoipBackend.MyBuddyCallback(self.notification_cb))
                buddy.subscribe()
                logger.debug( 'REMOTE USER ADDED')
                # add the buddy to the buddy dict
                buddies_dict[dest_uri]= buddy
                
              
                self.notification_cb(VoipEventType.BUDDY_EVENT,VoipEvent.BUDDY_SUBSCRIBED, {'success': True, 'buddy' : VoipBackend.SipBuddy(buddy)})
                #self.sip_controller.change_state(SipControllerState.Remote_user_subscribed, buddy_extension)
            except pj.Error, e:
                logger.exception( "ADDING REMOTE USER FAILED: Exception: " + str(e))
                #self.sip_controller.do_fsm(SipControllerState.Remote_user_subscribing_failed,buddy_extension)
                self.notification_cb(VoipEventType.BUDDY_EVENT,VoipEvent.BUDDY_SUBSCRIPTION_FAILED, {'success': False, 'buddy': VoipBackend.SipBuddy(buddy), 'error' :str(e)})
                #self.sip_controller.change_state(SipControllerState.Remote_user_subscribing_failed, buddy_extension)
    
    
        def delete_buddy(self, extension):
            logger.debug( "\nDeleting buddy with extension: %s" % extension)
            global buddies_dict
            if self.get_buddy(extension).get_state()== BuddyState.NOT_FOUND:
                return False
            else:
                buddy = buddies_dict[self._get_buddy_uri_key(extension)]
                buddy.unsubscribe()
                buddy.delete()
                del buddy
                buddy = None
                del buddies_dict[self._get_buddy_uri_key(extension)]
                return True
            
        def _delete_buddies(self):    
            logger.debug("BUDDIES TO DELETE:%s" % buddies_dict.keys())
            for b in self.get_buddies():
                self.delete_buddy(b.get_extension())                                             
            
        def get_buddy(self, extension):
            global buddies_dict
            dest_uri = self._get_buddy_uri_key(extension)
            if not buddies_dict.has_key(dest_uri):
                return VoipBackend.SipBuddy(None)
            else:
                return VoipBackend.SipBuddy(buddies_dict[dest_uri])
               
        def get_buddies(self):
            global buddies_dict
            buddies = []
            
            for b in buddies_dict.values():
                buddies.append(VoipBackend.SipBuddy(b))
            return buddies
            
        
             
    
    class SipBuddy(IBuddy):
        def __init__(self, pjBuddy):
            self.pjBuddy = pjBuddy
            
        def get_state(self):
            if (not self.pjBuddy):
                return  BuddyState.UNKNOWN
            elif (self.pjBuddy.info().online_status==1 and self.pjBuddy.info().online_text != 'On hold'):
                return BuddyState.ON_LINE
            elif self.pjBuddy.info().online_status==1 and self.pjBuddy.info().online_text == 'On hold':
                return BuddyState.ON_HOLD
            elif self.pjBuddy.info().online_status==2:
                return BuddyState.OFF_LINE
            else:
                return BuddyState.UNKNOWN
        
        def get_status_text(self):
            if (not self.pjBuddy):
                return "Not Found"
            else:
                return self.pjBuddy.info().online_text
            
        def get_uri(self):
            if (not self.pjBuddy):
                return "N.A"
            else:
                return self.pjBuddy.info().uri
        
        def get_extension(self):
            if (not self.pjBuddy):
                return "N.A"
            else:
                uri = self.get_uri()
                return uri[uri.find(":")+1:uri.find("@")]

            

    
    class SipCall(ICall):
        def __init__(self, local_uri, remote_uri, state):
            self.local_uri = local_uri
            self.remote_uri = remote_uri
            self.state = state
        
        def get_local_uri(self):
            return self.local_uri
        
        def get_remote_uri(self):
            return self.remote_uri
        
        def get_state(self):
            return self.state
        
            
            
            
    class SipServer(IServer):
        def __init__(self, ip, state):
            self.ip = ip
            self.state = state
        
        def get_ip(self):
            return self.ip
        
        def get_state(self):
            return self.state
        
        
        
       
# Callback to receive events from Call
    class MyCallCallback(pj.CallCallback):


        def __init__(self, notification_cb,call=None):
            pj.CallCallback.__init__(self, call)
            
            logger.debug("Istanziata MyCallCallback")
            self.notification_cb = notification_cb

        def on_replace_request(self, code, reason):
            logger.debug("richiamata ON REPLACE REQUEST:%s , %s" % (code,reason))
            return (code,reason)


        # Notification when call state has changed
        def on_state(self):
            global current_call, callState, local_hungup #, is_buddy_on_line

            
            logger.debug( "STATO DELLA CALL (on_state):%s" %  self.call.info().state)



            uri_to_call = self.call.info().remote_uri
            logger.debug( "Call with %s is %s Last code=%s (%s)" % (uri_to_call,
                                                                    self.call.info().state_text,
                                                                    self.call.info().last_code,self.call.info().last_reason))
             

            if self.call.info().state == pj.CallState.DISCONNECTED:
                current_call = None
                callState = CallState.IDLE

                logger.debug( "DISCONNECTION:Stopping call sound")
                _stop_call_sound()

                logger.debug( 'Current call is:%s' % current_call)
                
                if local_hungup:
                    logger.debug('Change internal state on Hanging up from on_state dopo HANGUP')
                    self.notification_cb(VoipEventType.CALL_EVENT, VoipEvent.CALL_HANGUP, {'success': True, 'call_state' :callState})
                    local_hungup = False
                else:
                    if is_server_on:   #is_buddy_on_line:
                        logger.debug( 'Change internal state on HANGUP from on_state  dopo REMOTE HUNGUP')
                        self.notification_cb(VoipEventType.CALL_EVENT, VoipEvent.CALL_REMOTE_HANGUP, {'success': True, 'call_state' :callState})
                    else:
                        logger.debug('Change internal state on HANGUP from on_state  dopo REMOTE DISCONNECTION HANGUP')
                        self.notification_cb(VoipEventType.CALL_EVENT,VoipEvent.CALL_REMOTE_DISCONNECTION_HANGUP, {'success': True, 'call_state' :callState})
                        #self.sip_controller.change_state(SipControllerState.RemoteDisconnectionHangup,callState)


            elif self.call.info().state==pj.CallState.CALLING:
                logger.debug("Dialing call to %s" % uri_to_call)
                callState = CallState.DIALING
                self.notification_cb(VoipEventType.CALL_EVENT,VoipEvent.CALL_DIALING, {'success': True, 'call_state' :callState})
                
                
            elif self.call.info().state==pj.CallState.CONFIRMED:
                logger.debug( "CALL CONFIRMED") #. sending REQUEST to %s" % uri_to_call
                #logger.debug('Change internal state on CALLING')
                #self.sip_controller.change_state(SipControllerState.Calling, callState)
                callState = CallState.ACTIVE
                _stop_call_sound()
                self.notification_cb(VoipEventType.CALL_EVENT,VoipEvent.CALL_ACTIVE, {'success': True, 'call_state' :callState})
                





        # Notification when call's media state has changed.
        def on_media_state(self):
            
            global callState, input_volume, output_volume, current_call
            
            logger.debug( 'DENTRO ON MEDIA STATE:%s' % self.call.info().media_state)
            if self.call.info().media_state == pj.MediaState.ACTIVE:
                logger.debug( "Stopping ring tone....")
                _stop_call_sound()
                # Connect the call to sound device
                call_slot = self.call.info().conf_slot
                pj.Lib.instance().conf_connect(call_slot, 0)
                pj.Lib.instance().conf_connect(0, call_slot)
                
                logger.debug( "VOLUME INIZIALE:" + str(pj.Lib.instance().conf_get_signal_level(call_slot)))
                pj.Lib.instance().conf_set_rx_level(call_slot,input_volume)
                pj.Lib.instance().conf_set_tx_level(call_slot,output_volume)


                logger.debug( "Media is now active")
                
                if callState ==CallState.HOLDING:
                    callState = CallState.ACTIVE
                    self.notification_cb(VoipEventType.CALL_EVENT,VoipEvent.CALL_UNHOLDING, {'success': True, 'call_state' :callState})
                else:
                    callState = CallState.ACTIVE
                    self.notification_cb(VoipEventType.CALL_EVENT,VoipEvent.CALL_ACTIVE, {'success': True, 'call_state' :callState})
                    

                #uri_to_call = self.call.info().remote_uri
                #print "CALL CONFIRMED. sending REQUEST to::: %s" % uri_to_call
                #self.call.send_pager(uri_to_call, "messaggio con la uri!!!", im_id="12345", content_type='text/plain')  #, hdr_list=["user=admin","secret=secret5"])
            elif self.call.info().media_state == pj.MediaState.LOCAL_HOLD:
                logger.debug( 'Local Hold request')
                callState = CallState.HOLDING
                #self.sip_controller.change_state(SipControllerState.Holding, callState)
                self.notification_cb(VoipEventType.CALL_EVENT,VoipEvent.CALL_HOLDING, {'success': True, 'call_state' :callState})

            elif self.call.info().media_state == pj.MediaState.REMOTE_HOLD:
                logger.debug( "Media is REMOTE HOLD STATE")
            else:
                logger.debug( "Media is inactive")

                #self.messenger.send_info("No Call")
                #self.messenger.update_call_button_label("Call")
                
                callState = CallState.IDLE
                _stop_call_sound()
                current_call = None



    class MyBuddyCallback(pj.BuddyCallback):
        def __init__(self, notification_cb, buddy=None):
            pj.BuddyCallback.__init__(self, buddy)
            self.notification_cb = notification_cb

        def on_state(self):
            logger.debug("Buddy %s is --> %s -> %s <--" % ( self.buddy.info().uri,
                                                                 self.buddy.info().online_status,
                                                                 self.buddy.info().online_text))
            

            #global is_buddy_on_line, is_buddy_on_hold, buddy_status_text, callState
            global callState
            is_buddy_on_line = self.buddy.info().online_status==1 and self.buddy.info().online_text != 'On hold'
            is_buddy_on_hold = self.buddy.info().online_status==1 and self.buddy.info().online_text == 'On hold'
            is_buddy_off_line = self.buddy.info().online_status==2
            if is_buddy_on_line:
                logger.debug('mando change state di Buddy CONNECTED')
                #self.sip_controller.change_state(SipControllerState.Remote_user_connected, buddy_status_text)
                self.notification_cb(VoipEventType.BUDDY_EVENT, VoipEvent.BUDDY_CONNECTED, {'buddy' : VoipBackend.SipBuddy(self.buddy)})
            elif is_buddy_on_hold:
                self.notification_cb(VoipEventType.BUDDY_EVENT,  VoipEvent.BUDDY_HOLDING, {'buddy' : VoipBackend.SipBuddy(self.buddy)})
            elif is_buddy_off_line:
                logger.debug('mando change state di Buddy DISCONNECTED')
                #self.sip_controller.change_state(SipControllerState.Remote_user_disconnected, buddy_status_text)
                self.notification_cb(VoipEventType.BUDDY_EVENT, VoipEvent.BUDDY_DISCONNECTED, {'buddy' : VoipBackend.SipBuddy(self.buddy)})
            else:
                logger.debug('buddy is in a unknown state')
                

            #self.messenger.update_status_label(SignalEmitterState.NO_CALL)

        def on_pager(self, mime_type,body):
            logger.debug( "Instant message in BuddyCallBack from %s (%s)" % (self.buddy.info().uri, mime_type))
             
            logger.debug("Message body:%s" % body)

        def on_pager_status(self, body, im_id, code, reason):
            if code >= 300:
                logger.debug("Message delivery failed for message %s to %s: %s" % (body, self.buddy.info().uri,reason))
                
                

        def on_typing(self, is_typing):
            if is_typing:
                logger.debug("%s is typing" % self.buddy.info().uri)
            else:
                logger.debug("%s is typing" % self.buddy.info().uri)


 
    
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
            self.OK = 200
            self.SERVICE_UNAVAILABLE = 503


        
        def on_incoming_subscribe(self, my_buddy, from_uri, contact_uri, pres_obj):
            print '\n\nrichiamato on subscribe del buddy:%s da %s a %s ' % (my_buddy, from_uri, contact_uri)

            return (200,None)
  
        
        # Notification on local user registration change state (a request timeout status code implies that the sip server is disconnected)
        def on_reg_state(self):
            logger.debug( '\ncalled on reg status:%s' % self.account.info().reg_status)
            logger.debug( 'called on reg reason:%s\n' % self.account.info().reg_reason)
            logger.debug( 'on line text:%s' % self.account.info().online_text)
            logger.debug( 'account is registered?:%s' % self.account.info().reg_active)
            
            global is_server_on, account_state
            # utente registrato con successo (evidentemente il server e' su
            reg_status = self.account.info().reg_status
            reg_reason = self.account.info().reg_reason
            #unregistration_request = self.account.info().reg_expires == 0
            reg_is_active = self.account.info().reg_active and  self.account.info().reg_expires > 0


            is_server_on = not reg_status in [self.REQUEST_TIMEOUT, self.SERVICE_UNAVAILABLE];

            if reg_status in [self.REQUEST_TIMEOUT, self.SERVICE_UNAVAILABLE]:
                #self.sip_controller.change_state(SipControllerState.Connection_failed, {'reg_status': reg_status, 'reg_reason': reg_reason})
                #self.sip_controller.do_fsm(SipControllerState.Connection_failed,{'reg_status': reg_status, 'reg_reason': reg_reason})
                account_state = AccountState.UNREGISTERED
                self.notification_cb(VoipEventType.LIB_EVENT, VoipEvent.LIB_CONNECTION_FAILED, {'Success' : False, 'reg_status': reg_status, 'reg_reason': reg_reason})
                self.already_registered = False
               
                
            elif reg_status==self.OK:
                if (not reg_is_active):
                    self.already_registered = False
                    account_state = AccountState.UNREGISTERED
                    self.notification_cb(VoipEventType.ACCOUNT_EVENT ,VoipEvent.ACCOUNT_UNREGISTERED, {'Success' : True, 'reg_status': reg_status})
                elif not (self.already_registered and reg_is_active):
                    logger.debug("LOCAL USER OK")
                    #self.sip_controller.change_state(SipControllerState.Registered,self.account.info())
                    #self.sip_controller.do_fsm(SipControllerState.Registered,self.account.info())
                    account_state = AccountState.REGISTERED
                    self.notification_cb(VoipEventType.ACCOUNT_EVENT ,VoipEvent.ACCOUNT_REGISTERED, {'Success' : True, 'reg_status': reg_status })
                    self.already_registered = True
                    
            else:
                if (not reg_is_active):
                    logger.debug( 'LOCAL USER REGISTRATION FAILED:%s, %s' % (reg_status,reg_reason))
                    #self.sip_controller.change_state(SipControllerState.Registration_failed, {'reg_status': reg_status, 'reg_reason': reg_reason})
                    #self.sip_controller.do_fsm(SipControllerState.Registration_failed,{'reg_status': reg_status, 'reg_reason': reg_reason})
                    self.notification_cb(VoipEventType.ACCOUNT_EVENT , VoipEvent.ACCOUNT_REGISTRATION_FAILED, {'Success' : False, 'reg_status': reg_status, 'reg_reason': reg_reason})
                else:
                    logger.debug( 'LOCAL USER UNREGISTRATION FAILED:%s, %s' % (reg_status,reg_reason))
                    #self.sip_controller.change_state(SipControllerState.Registration_failed, {'reg_status': reg_status, 'reg_reason': reg_reason})
                    #self.sip_controller.do_fsm(SipControllerState.Registration_failed,{'reg_status': reg_status, 'reg_reason': reg_reason})
                    self.notification_cb(VoipEventType.ACCOUNT_EVENT , VoipEvent.ACCOUNT_UNREGISTRATION_FAILED, {'Success' : False, 'reg_status': reg_status, 'reg_reason': reg_reason})
                    

        # Notification on incoming call
        def on_incoming_call(self, call):
            logger.debug( '\nINCOMING CALL FROM %s \n' % call.info().remote_uri)
            global current_call,config, callState, refused, auto_answer, auto_answer_delay
            refused = False
            
            if current_call:
                callState = CallState.ACTIVE
                logger.debug( 'Busy call... Refusing that')
                #call.answer(486, "Busy") # not used because app craches!
                call.hangup()
                return
            #ru = str(call.info().remote_uri)
            #remote_contact = ru[ru.index('"')+1:ru.rindex('"')]
            logger.debug( "Incoming call from %s" % call.info().remote_uri)

        
            callState = CallState.INCOMING
            #_start_call_sound(config.get("VoipBackend","in_call_ring_tone"))
            _start_call_sound_in()
            current_call = call
            call_cb = VoipBackend.MyCallCallback(self.notification_cb,current_call)
            current_call.set_callback(call_cb)

            logger.debug( 'cambio lo stato interno del sip controller in incoming')
            self.notification_cb(VoipEventType.CALL_EVENT,VoipEvent.CALL_INCOMING, {'Success' : True, 'from': call.info().remote_uri})
            if auto_answer==True:
                logger.debug( "auto answering after %s seconds" % auto_answer_delay)

                #self.messenger.send_info("Auto Answering to Incoming call from %s in %s seconds" % (remote_contact,delay))
                #self.messenger.update_status_label("Auto Answering to Incoming call from %s in %s seconds" % (remote_contact,delay))
                #self.messenger.update_status_label(SignalEmitterState.AUTO_ANSWERING_CALL)
                
                #TODO: sostituire il time sleep con il timer.add

                current_call.answer(180)
                self.auto_answer_call = self.sip_controller.loop.timer_add(auto_answer_delay*1000,self._auto_answer) ## TODO
#             else:
#                 current_call.answer(180)

        def _auto_answer(self):
            global current_call, callState
            logger.debug( 'RISPOSTA AUTOMATICA!')
            self.auto_answer_call = None
             
            if callState==CallState.INCOMING:
                current_call.answer(200)


    def _initialize_values(self):
        logger.debug( 'setting audio values as default values (embedded)')
         
        global input_volume, output_volume, auto_answer, auto_answer_delay
        #config = TecapConfig().getConfig()

        input_volume = 0.5 # config.getfloat('VoipBackend','input_volume')
        output_volume = 0.5 # config.getfloat('VoipBackend','output_volume')
        auto_answer = False #config.getboolean('VoipBackend','auto_answer')
        auto_answer_delay = 3 #config.getint('VoipBackend','auto_answer_delay')

        #self.sip_controller.view.control_panel.mic_volume.slider.setValue(input_volume*100)
        #self.sip_controller.view.control_panel.out_volume.slider.setValue(output_volume*100)
        logger.debug( "reading audio I/O devices...")
        in_device = -1 # config.getint('VoipBackend','in_device')
        out_device = -2 # config.getint('VoipBackend','out_device')
        sel_in = -1 # config.getint('VoipBackend','in_sel_device')
        sel_out = -1 # config.getint('VoipBackend','out_sel_device')
        logger.debug( "Available devices:%s" % str(self.lib.enum_snd_dev()))
        self.set_audio_devices(in_device,out_device,sel_in,sel_out)
        

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
        logger.debug( 'into set_audio_devices()')
        try:
            self.lib.set_snd_dev(input_device, output_device)
            self.input_dev_list_index =  list_index_in
            self.output_dev_list_index =  list_index_out
        except Exception , ex:
            logger.debug( "Eccezione:%s" % str(ex))

    def set_call_preferences(self, auto_answering, auto_answering_delay):
        logger.debug( 'into set_call_preferences on Voip Api backend')
        global auto_answer, auto_answer_delay
        try:
            auto_answer = auto_answering
            auto_answer_delay = auto_answering_delay
        except Exception , ex:
            logger.error( "Exception:%s" % str(ex))


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
        logger.debug( "setting input volume   to:%s" % input_volume)
        global current_call
        if current_call:
            slot = current_call.info().conf_slot
            if slot!=None and slot>-1:
                pj.Lib.instance().conf_set_tx_level(slot,input_volume)
                #pj.Lib.instance().conf_set_rx_level(slot,input_volume)
            else:
                logger.debug( "SLOT NULLO!")

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
             
            if slot!=None and slot>-1:
                pj.Lib.instance().conf_set_rx_level(slot, output_volume)
            else:
                logger.debug( "SLOT NULLO!")
    
    def unregister_account(self):
        
        if self.is_server_on():

            logger.debug("Unregistering account having active? %s is_valid? %s" % (acc.info().reg_active, acc.is_valid()))
            try:
                self.notification_cb(VoipEventType.ACCOUNT_EVENT, VoipEvent.ACCOUNT_UNREGISTERING, {'Success' : True, 'account_info' : self.my_account[0]})
                if acc.info().reg_active:
                    acc.set_registration(False)
                    #acc.delete()
                    #acc = None
                    return True
            except Exception,ex:
                logger.exception("Unexpected exception during user unregistration, maybe because the sip server is offline:%s" % ex)
                self.notification_cb(VoipEventType.ACCOUNT_EVENT,VoipEvent.ACCOUNT_UNREGISTERED, {'Success' : False, 'account_info' : self.my_account[0]})
                return False

            
    
    def destroy_lib(self):
        if (self.lib==None):
            return False
        
        self.notification_cb(VoipEventType.LIB_EVENT,VoipEvent.LIB_DEINITIALIZING, {'Success' : True, 'account_info' : self.my_account[0]})
        if not self.lib:
            logger.warn('No Pjsip lib to shutdown')
            self.notification_cb(VoipEventType.LIB_EVENT,VoipEvent.LIB_DEINITIALIZATION_FAILED, {'Success' : False, 'account_info' : self.my_account[0], 'reason': 'No Voip Lib to shutdown' })
            return False
        
        logger.debug( 'PJSIP SHUTTING DOWN....')
        global acc
        global transport
        global current_call
        global player_out_id
        global player_in_id


        # _stop_call_sound()

        # if player_out_id:
        #     pj.Lib.instance().player_destroy(player_out_id)
        # if player_in_id:
        #     pj.Lib.instance().player_destroy(player_in_id)
        
        logger.debug('Deallocating players...')
        
        if player_out_id:
            logger.debug( 'deleting out id')
            del player_out_id
            player_out_id = None
        if player_in_id:
            logger.debug( 'deleting in id')
            del player_in_id
            player_in_id = None


        # lo shutdown della libreria non e' consentito se c'e' una call in corso
#        if current_call:
#            self.hangup_call()
#            while self.is_current_call():
#                time.sleep(1)
         
        # self.unregister_account() # explicit unregistration ???
        
        
        
        
        logger.debug('Deallocating buddies...')
        try:
            self.get_account()._delete_buddies()
                               
            logger.debug('Deallocating account...')  
            if acc!=None:
                acc.set_basic_status(False)
                acc.set_registration(False)
                acc.delete()
                del acc
                acc = None
            
            # Shutdown the library
            
              
            logger.debug('Deallocating transport...')
#             if (transport!=None): # TODO verificare se la variabile transport va usata...
#                 #lck = self.lib.auto_lock()
#                 
#                 transport.close(True)
#                 logger.debug( "After force transport close")
#                 del transport
#                 transport = None
#                 #del lck
                

                
                
            logger.debug( "destroying pjsip lib")
            self.lib.destroy()
            del self.lib
            self.lib = None
            
            logger.debug( "library destroyed")
            
        except Exception, e:
            logger.exception( 'Exception during shutting down:%s' % e)
            self.notification_cb(VoipEventType.LIB_EVENT,VoipEvent.LIB_DEINITIALIZATION_FAILED, {'Success' : False, 'account_info' : self.my_account[0], 'reason': str(e)})
            return False
        self.notification_cb(VoipEventType.LIB_EVENT,VoipEvent.LIB_DEINITIALIZED, {'Success' : True, 'account_info' : self.my_account[0]})
        return True
    
  
        
    def init_lib(self,params, notification_cb):
        global logger
        #logging.getLogger("Voip").debug("\nCalled init_lib method!!!\n")
        # Create library instance
        # Create pjsua before anything else
        
        self.notification_cb = notification_cb
        self.params = params
        
        self.notification_cb(VoipEventType.LIB_EVENT,VoipEvent.LIB_INITIALIZING, {'success' :True, 'params': params})
        
        if (self.params.has_key("debug") and self.params["debug"]==False):
            logger.disabled = True
              
        logger.debug("\nSTARTING SIP...%s\n" % str(params))
        if not self.lib:
            logger.debug("\nInstancing pjsip lib...%s\n" % str(params))
            self.lib = pj.Lib()
        
  
        
        
        self.sip_server = str(self.params['sip_server_address'])
        if (self.params.has_key("turn_server_address")):
            self.turn_server = str(self.params['turn_server_address'])
        else:
            self.turn_server= None
            
        logger.debug( 'reading server: %s:%s' % (self.sip_server, self.turn_server))
        self.my_account = (self.params['sip_server_user'], self.params['sip_server_pwd'])
        logger.debug("ACCOUNT FROM WEB APP:%s,%s" % self.my_account)

        try:
            # Init library with default config and some customized
            # TURN SERVER CONFIGURATION
            
            my_media_cfg = pj.MediaConfig()
            
            if (self.turn_server!=None):
                my_media_cfg.enable_ice = True
                my_media_cfg.enable_turn = True
                my_media_cfg.turn_server = "%s:3478" % str(self.turn_server)
                my_media_cfg.turn_conn_type = pj.TURNConnType.TCP
                
                logger.debug("Setting turn server[%s]:%s" % (type(my_media_cfg.turn_server), (my_media_cfg.turn_server)))
                
                if self.params.has_key("turn_server_user"):
                    my_media_cfg.turn_cred = pj.AuthCred("most.crs4.it", '%s' % str(self.params['turn_server_user']), '%s' % str(self.params['turn_server_pwd'])) #TODO check remote.most.it
                    logger.debug("Setting turn user:%s:%s" % (self.params['turn_server_user'], self.params['turn_server_pwd']))
                    logger.debug("#%s#" % my_media_cfg.turn_cred)
                else:
                    logger.debug("No turn server user specified...")
                
                
                
            #my_media_cfg.snd_play_latency = 0
            #my_media_cfg.snd_rec_latency = 0
            my_media_cfg.jb_max = 1000

            my_media_cfg.no_vad = True
            my_media_cfg.quality = 10
            
            #my_media_cfg.snd_clock_rate = 22050
            #my_media_cfg.ptime = 20
            LOG_LEVEL =  self.params["log_level"]

            ua_cfg = pj.UAConfig()
         
            
            self.lib.init(log_cfg = pj.LogConfig(level=LOG_LEVEL, callback=log_cb),  media_cfg=my_media_cfg, ua_cfg=ua_cfg)

            
            # TRANSPORT CONFIGURATION
            #print "creating UDP transport..."
            global transport
            if (self.params.has_key("sip_server_transport") and self.params["sip_server_transport"]=="udp"):
                transport = self.lib.create_transport(pj.TransportType.UDP)
            else:
                transport = self.lib.create_transport(pj.TransportType.TCP)
            logger.debug("Creating transport:%s" % transport)

            # Start the library
            self.lib.start(with_thread=True)


            self._initialize_values()
            self.notification_cb(VoipEventType.LIB_EVENT,VoipEvent.LIB_INITIALIZED, {'success': True, 'sip_server' :self.sip_server})
            #self.sip_controller.change_state(SipControllerState.Initialized, self.sip_server)
            #self.sip_controller.do_fsm(SipControllerState.Initialized,self.params)

            logger.debug('SIP successfully initialized!')
            return True
        except pj.Error, e:
            self.notification_cb(VoipEventType.LIB_EVENT,VoipEvent.LIB_INITIALIZATION_FAILED, {'success': False, 'sip_server' :self.sip_server ,'error' :str(e)})
            logger.exception( "SIP INITIALIZATION FAILED: Exception: " + str(e))
            if self.lib!=None:
                self.lib.destroy_lib()
            self.lib = None
            return False
        
            #self.sip_controller.change_state(SipControllerState.Initialize_failed, str(e))
            self.notification_cb(VoipEventType.LIB_EVENT,VoipEvent.LIB_INITIALIZATION_FAILED, {'success': False, 'error' :str(e)})
            #self.sip_controller.do_fsm(SipControllerState.Initialize_failed,self.params)


    def set_online_status(self, is_online):
        global acc
        if acc:
            acc.set_basic_status(is_online)
         
    def is_online(self):
        if not acc:
            return False
        
        return acc.info().online_status   
    
    def register_account(self):
        try:
            global acc

            if not acc:

                logger.debug("Registering account **%s** (PWD:%s) on sip server:**%s**" % (self.my_account[0],self.my_account[1], self.sip_server))
                if (self.params.has_key("sip_server_transport") and self.params["sip_server_transport"]=="udp"):
                    transport_info = "" 
                else:
                    transport_info =  ";transport=tcp"
                                  
                acc_cfg = pj.AccountConfig('%s%s' % (str(self.sip_server), transport_info), str(self.my_account[0]), str(self.my_account[1]))
    
                logger.debug("Account Config:%s" % acc_cfg.reg_uri)
               
                # la riregistrazione avviene ogni 60 secondi che e' il minimo consentito (verifica la presenza del server)
                acc_cfg.reg_timeout = 60
                acc_cfg.publish_enabled = True
                

                acc = self.lib.create_account(acc_cfg, cb=VoipBackend.MyAccountCallback(self.notification_cb))  

                logger.debug("Account %s registration successfully sent with timeout:%s" % (self.my_account[0], acc_cfg.reg_timeout))
                #self.sip_controller.change_state(SipControllerState.Registered, self.my_account[0])
                #self.sip_controller.do_fsm(SipControllerState.Registered,self.my_account[0])
                self.notification_cb(VoipEventType.ACCOUNT_EVENT,VoipEvent.ACCOUNT_REGISTERING, {'Success' : True, 'account_info' : self.my_account[0]})
            else:
                logger.debug("account previously registered. Nothing to do")
            
            return True

            
        except pj.Error, e:
            logger.exception("Exception registering account:%s" % str(e))
            self.lib.destroy_lib()
            self.lib = None
            #self.sip_controller.change_state(SipControllerState.Registration_failed, str(e))
            #self.sip_controller.do_fsm(SipControllerState.Registration_failed,self.params)
            self.notification_cb(VoipEventType.ACCOUNT_EVENT,VoipEvent.ACCOUNT_REGISTRATION_FAILED, {'Success' : False, 'error' : str(e), 'params': self.params})
            return False

    def _show_devices(self):
        
        logger.debug( "looking for installed devices...")
        devices = self.lib.enum_snd_dev()
        logger.debug( "Devices found:%s" % len(devices))
        assert len(devices)>0
        for d in devices:
            logger.debug( "DEVICE: %s > sr:%s"  % (d.name,str(d.default_clock_rate)))
            
        snd_dev = self.lib.get_snd_dev()
        logger.debug( "My Sound devs:%s" % str(snd_dev)) ## returns (-1,-2)
        
    def _show_codecs(self):
        c = self.lib.enum_codecs()
        logger.debug("List of Codecs(%s)" % len(c))
        for codec in c:
            logger.debug("Found codec %s with priority: %s" % (codec.name, codec.priority))
            if not codec.name.startswith('iLBC'):pass
                #self.lib.set_codec_priority(codec.name, 0)
                #print 'Setting codec priority to 0 for %s' % codec.name
                #codec.priority=0


    
    
    def get_account(self):
        return VoipBackend.SipAccount(self.notification_cb, self.sip_server)
        
                
    def get_server(self):
        global  is_server_on
        
        if is_server_on:
            return VoipBackend.SipServer(self.sip_server, ServerState.CONNECTED)
        else:
            return VoipBackend.SipServer(self.sip_server, ServerState.DISCONNECTED)
   

    # Function to make call
    def make_call(self,dest_extension):

        global current_call, config
        global acc, callState
        if not dest_extension:
            logger.debug( "No sip address provided. Call canceled")
            return False
        
        uri = 'sip:%s@%s' % (str(dest_extension), str(self.sip_server))

        logger.debug( "MAKE CALL:%s" % str(current_call))
        logger.debug( "CALL STATE(make_call): %s " % str (callState))
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



            logger.debug( "Making call to:%s" % uri)
            #self.messenger.send_info("Dialing to %s (%s)" % (contact, uri))
            #self.messenger.update_status_label("Dialing call to %s" % uri)
            #self.messenger.update_status_label(SignalEmitterState.OUTCOMING_CALL)
            lck = self.lib.auto_lock()

            logger.debug( "CURRENT CALL PRIMA:%s" % current_call)


            
            current_call = acc.make_call(uri, cb=VoipBackend.MyCallCallback(self.notification_cb)) # todo inserire Listener
            #callState = CallState.DIALING
            _start_call_sound_out()
            #self.messenger.update_call_button_label("Hangup")
            
            del lck
            
            return True
           

        except pj.Error, e:
            logger.exception( ">>>> Exception in make_call: %s" % str(e))
            return False
        
        return False

    def answer_call(self):
        global current_call

        try:
            _stop_call_sound()
            
            if not current_call:
                logger.debug( 'There is no call TO ANSWER!')
                
                return
            elif current_call.info().state!=pj.CallState.CONFIRMED:
                current_call.answer(200)
                logger.debug( 'Answer')
            else:
                logger.debug( 'Call On line: answer call ignored')
                
        except Exception, ex:
            logger.exception( 'Exception in answer call:%s' % ex)
            
        except pj.Error,er:
            logger.exception("Pjsip Error in answer call:%s" % er) 
            return
            
    
    
    def hangup_call(self):
        #global refused
        #refused = True
        logger.debug( "HANGUP Request")
        try:
            _stop_call_sound()

            global current_call, local_hungup

            if current_call:
                logger.debug( "Hangup Call (current_call!=null).")
                local_hungup = True
                current_call.hangup()
                current_call = None
                return True
            else:
                logger.debug("There is no call to hangup")
                #self.messenger.send_info("No Call to hangup!")
                return False

        except Exception,ex:
            logger.exception("Exception in hangup call:%s" % ex) 
            return False
        
        except pj.Error,er:
            logger.exception("Pjsip Error in hangup call:%s" % er) 
            return False


    def hold_call(self):
        try:
            global current_call
            if current_call:
                logger.debug( 'holding current call...')
                current_call.hold()
                self.notification_cb(VoipEventType.CALL_EVENT,VoipEvent.CALL_HOLDING, {'success': True})
                #logger.debug("remote holding!")
                return True
            else:
                logger.debug( "There is no call to hold")
                #self.messenger.send_info("No Call to hungup!")
                return False
        except Exception,ex:
            logger.exception("Exception in hold_call:%s" % ex)
            return False
        except pj.Error,er:
            logger.exception("Pjsip Error in hold call:%s" % er) 
            return False

    def unhold_call(self):
        try:
            global current_call
            if current_call:
                current_call.unhold()
                self.notification_cb(VoipEventType.CALL_EVENT,  VoipEvent.CALL_ACTIVE, {'success': True})
                logger.debug( "unholding!")
                return True
            else:
                logger.debug( "There is no call to unhold")
                #self.messenger.send_info("No Call to hungup!")
                return False
        except Exception,ex:
            logger.exception( "Exception in unhold_call:%s" % ex)
            return False
        except pj.Error,er:
            logger.exception("Pjsip Error in unhold call:%s" % er) 
            return False


    def get_call(self):
        global callState
        if (not current_call):
            #callState = CallState.IDLE # bug fixing...?
            return VoipBackend.SipCall("","", callState)
        else:
            return VoipBackend.SipCall(current_call.info().uri,current_call.info().remote_uri, callState)
    
    def serialize_values(self):
        if not self.lib:
            logger.debug( 'PJSIP Library no yet initialized. Nothing to serialize!!')
            return
        
        logger.debug( 'serializing audio preferences TEMPORARY DISABLED!')

        global input_volume, output_volume, auto_answer, auto_answer_delay
        """
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
        """

