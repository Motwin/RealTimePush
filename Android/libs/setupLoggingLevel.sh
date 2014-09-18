# execute this script in android sdk folder to enable Motwin SDK logging
# MAX TAG LENGTH          012345678901234567890123 DEBUG
						  
adb shell setprop log.tag.MotwinSDK 			   DEBUG
adb shell setprop log.tag.ClientChannel 		   DEBUG
adb shell setprop log.tag.AbstractClientChannel	   DEBUG
adb shell setprop log.tag.SocketListener 		   DEBUG
adb shell setprop log.tag.ConnectionHandler 	   DEBUG
adb shell setprop log.tag.ConnectionInputHandler   DEBUG
adb shell setprop log.tag.ImmediateSendQueue 	   DEBUG
adb shell setprop log.tag.InternalResolvers 	   DEBUG
adb shell setprop log.tag.MttConnectionMonitor 	   DEBUG
adb shell setprop log.tag.MttDecoder 			   DEBUG
adb shell setprop log.tag.MttEncoder 			   DEBUG
adb shell setprop log.tag.MttListenThread 		   DEBUG
adb shell setprop log.tag.MttMobile 			   DEBUG
adb shell setprop log.tag.SocketFactory 		   DEBUG
adb shell setprop log.tag.RequestManager 		   DEBUG
adb shell setprop log.tag.MttMessageInputStream	   DEBUG
adb shell setprop log.tag.MttPrimitiveIS 		   DEBUG
adb shell setprop log.tag.MttPrimitiveOS 		   DEBUG
adb shell setprop log.tag.Database 				   DEBUG
adb shell setprop log.tag.Bucket		 		   DEBUG
adb shell setprop log.tag.BucketManager 		   DEBUG
adb shell setprop log.tag.CQController 			   DEBUG
adb shell setprop log.tag.CQManager 			   DEBUG
adb shell setprop log.tag.LifeCycleHelper		   DEBUG
adb shell setprop log.tag.Setter 				   DEBUG
adb shell setprop log.tag.ClientChannelListener	   DEBUG
adb shell setprop log.tag.ConnectivityManager	   DEBUG
adb shell setprop log.tag.MessageSender			   DEBUG
