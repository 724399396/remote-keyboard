module Main where

import Prelude hiding (lookup)
import System.IO
import System.Process
import Network.Socket
import Control.Exception
import Control.Concurrent
import Control.Monad
import Data.Map.Strict

cmdMap :: Map String String
cmdMap = fromList [ ("sForward", "shift+Right")
                  , ("mForward", "alt+Right")
                  , ("lForward", "ctrl+Right")
                  , ("sBackward", "shift+Left")
                  , ("mBackward", "alt+Left")
                  , ("lBackward", "ctrl+Left")
                  , ("previous", "p")
                  , ("next", "n")
                  , ("play", "space")
                  , ("full", "f")
                  ]

focus :: IO ()
focus = do
  callCommand "xdotool search --name \"VLC media player\" windowfocus"

withFocus :: IO () -> IO ()
withFocus action = focus >> action

sendCommand :: String -> IO ()
sendCommand cmd = withFocus $
  callCommand $ "xdotool key " ++ cmd

dispath :: Handle -> IO ()
dispath h = do
  input <- hGetLine h
  putStrLn $ "execute " ++ input
  case lookup input cmdMap of
    Just cmd -> sendCommand cmd
    Nothing -> return ()

net :: (Handle -> IO ()) -> IO ()
net action = do
  let hints = defaultHints { addrFlags = [AI_NUMERICHOST, AI_NUMERICSERV], addrSocketType = Stream }
  AddrInfo {addrAddress = addr, addrFamily = family, addrProtocol = protocol }:_ <- getAddrInfo (Just hints) (Just "0.0.0.0") (Just "6666")
  bracket (socket family Stream protocol) close $ \s -> do
    bind s addr
    putStrLn $ show addr
    listen s 5
    forever $ do (ns, _) <- accept s
                 forkIO (bracket (socketToHandle ns ReadMode) hClose $ \h -> 
                         do hSetBuffering h NoBuffering
                            action h)

main :: IO ()
main = net dispath
