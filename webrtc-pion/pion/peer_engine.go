package pion

import (
	"strconv"

	"github.com/pion/interceptor"
	"github.com/pion/webrtc/v4"
	"minceraft.dev/sonus/webrtc-pion/log"
)

func ConstructApi(handlerId uint32) *webrtc.API {
	loggerFact := SimplePionLoggerFactory{
		Logger:     log.Logger,
		NameSuffix: strconv.Itoa(int(handlerId)),
	}

	mediaEngine := webrtc.MediaEngine{}

	// TODO add more codecs
	// register rx codec
	if err := mediaEngine.RegisterCodec(webrtc.RTPCodecParameters{
		RTPCodecCapability: webrtc.RTPCodecCapability{
			MimeType: codec, ClockRate: sampleRate, Channels: rxChannels, SDPFmtpLine: "", RTCPFeedback: nil,
		},
		PayloadType: 42,
	}, webrtc.RTPCodecTypeAudio); err != nil {
		panic(err)
	}
	// register tx codec
	if err := mediaEngine.RegisterCodec(webrtc.RTPCodecParameters{
		RTPCodecCapability: webrtc.RTPCodecCapability{
			MimeType: codec, ClockRate: sampleRate, Channels: txChannels, SDPFmtpLine: "", RTCPFeedback: nil,
		},
		PayloadType: 84,
	}, webrtc.RTPCodecTypeAudio); err != nil {
		panic(err)
	}

	// some random defaults, Idk
	interceptorRegistry := interceptor.Registry{}
	if err := webrtc.RegisterDefaultInterceptorsWithOptions(
		&mediaEngine, &interceptorRegistry,
		webrtc.WithInterceptorLoggerFactory(&loggerFact),
	); err != nil {
		panic(err)
	}

	return webrtc.NewAPI(
		webrtc.WithMediaEngine(&mediaEngine),
		webrtc.WithInterceptorRegistry(&interceptorRegistry),
		webrtc.WithSettingEngine(webrtc.SettingEngine{
			LoggerFactory: &loggerFact,
		}),
	)
}
