package pionbound

import (
	"github.com/pion/webrtc/v4"
	"minceraft.dev/sonus/webrtc-pion/ipc"
	"minceraft.dev/sonus/webrtc-pion/ipc/callback"
	"minceraft.dev/sonus/webrtc-pion/ipc/protocol/buffer"
	"minceraft.dev/sonus/webrtc-pion/pion"
)

type IpcApiAllocatePeer struct {
	HandlerId          uint32
	IceServers         []webrtc.ICEServer
	IceTransportPolicy webrtc.ICETransportPolicy
	BundlePolicy       webrtc.BundlePolicy
	Id                 string
}

func (msg *IpcApiAllocatePeer) GetSonusboundId() uint8 {
	return 0xFF
}

func (msg *IpcApiAllocatePeer) GetPionboundId() uint8 {
	return 0x02
}

func (msg *IpcApiAllocatePeer) Decode(buf *buffer.ByteBuf) (err error) {
	// read data
	var iceTransportPolicy uint32
	var bundlePolicy uint32
	if msg.HandlerId, err = buf.ReadVarInt(); err != nil {
		return
	} else if msg.IceServers, err = buffer.ReadSlice(buf, func() (ret webrtc.ICEServer, err error) {
		// read data
		var url string
		var user *string
		var auth *string
		if url, err = buf.ReadUtf8(); err != nil {
			return
		} else if user, err = buffer.ReadNilable(buf, buf.ReadUtf8); err != nil {
			return
		} else if auth, err = buffer.ReadNilable(buf, buf.ReadUtf8); err != nil {
			return
		}
		// set to struct
		ret.URLs = []string{url}
		if user != nil {
			ret.Username = *user
		}
		if auth != nil {
			ret.Credential = *auth
			ret.CredentialType = webrtc.ICECredentialTypePassword
		}
		return
	}); err != nil {
		return
	} else if iceTransportPolicy, err = buf.ReadVarInt(); err != nil {
		return
	} else if bundlePolicy, err = buf.ReadVarInt(); err != nil {
		return
	} else if msg.Id, err = buf.ReadUtf8(); err != nil {
		return
	}
	// set to struct
	msg.IceTransportPolicy = webrtc.ICETransportPolicy(iceTransportPolicy)
	msg.BundlePolicy = webrtc.BundlePolicy(bundlePolicy)
	return
}

func (msg *IpcApiAllocatePeer) Encode(*buffer.ByteBuf) error {
	panic("unsupported")
}

func (msg *IpcApiAllocatePeer) Handle(handler *ipc.Handler) error {
	peer, err := pion.ConstructApi(handler.Id).NewPeerConnection(webrtc.Configuration{
		ICEServers:         msg.IceServers,
		ICETransportPolicy: msg.IceTransportPolicy,
		BundlePolicy:       msg.BundlePolicy,
	})
	if err != nil {
		return err
	}
	wrappedPeer := &pion.PionPeer{
		PeerConnection: peer,
		Callback:       &callback.HandlerCallback{Handler: handler},
	}
	if err = wrappedPeer.Initialize(msg.Id); err != nil {
		_ = wrappedPeer.Close()
		return err
	}
	handler.Peer = wrappedPeer
	return nil
}

func (msg *IpcApiAllocatePeer) GetHandlerId() uint32 {
	return msg.HandlerId
}
