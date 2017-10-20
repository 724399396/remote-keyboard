module Main where

import Prelude hiding (lookup)
import System.IO
import System.Process
import Network.Socket
import Control.Exception
import Control.Concurrent
import Control.Monad
import Data.Map.Strict
import Control.Lens
import Control.Lens.Tuple

data Command = Xdotool String
             | Normal String

cmdMap :: Map String Command
cmdMap = fromList $ (fmap (over _2 Xdotool)
                     [ ("sForward", "shift+Right")
                     , ("mForward", "alt+Right")
                     , ("lForward", "ctrl+Right")

                     , ("sBackward", "shift+Left")
                     , ("mBackward", "alt+Left")
                     , ("lBackward", "ctrl+Left")

                     , ("previous", "p")
                     , ("next", "n")
                     , ("play", "space")

                     , ("full", "f")

                     , ("uVolume", "ctrl+Up")
                     , ("dVolume", "ctrl+Down")
                     , ("mute", "m")

                     ,("close", "alt+F4")
                     ]) ++
         (fmap (over _2 Normal)
          [("open", "vlc /media/weili/New\\ Volume/Program")
          ,("shutdown", "shutdown")])

focus :: IO ()
focus = do
  callCommand "xdotool search --name \"VLC media player\" windowfocus"

withFocus :: IO () -> IO ()
withFocus action = focus >> action

sendCommand :: String -> IO ()
sendCommand cmd =
  callCommand $ "xdotool key " ++ cmd

dispath :: Handle -> IO ()
dispath h = (do
  input <- hGetLine h
  putStrLn $ "execute " ++ input
  case lookup input cmdMap of
    Just (Xdotool cmd) -> sendCommand cmd
    Just (Normal x) -> callCommand x
    Nothing -> return ()
  dispath h) `catch` (\e -> do
                         putStrLn (show (e :: IOException))
                         hClose h
                     )

net :: (Handle -> IO ()) -> IO ()
net action = do
  let hints = defaultHints { addrFlags = [AI_NUMERICHOST, AI_NUMERICSERV], addrSocketType = Stream }
  AddrInfo {addrAddress = addr, addrFamily = family, addrProtocol = protocol }:_ <- getAddrInfo (Just hints) (Just "0.0.0.0") (Just "6666")
  bracket (socket family Stream protocol) close $ \s -> do
    bind s addr
    putStrLn $ show addr
    listen s 1
    forever $ do (ns, n) <- accept s
                 putStrLn $ "connect from " ++ show n
                 forkIO (bracket (socketToHandle ns ReadMode) hClose $ \h -> 
                         do hSetBuffering h NoBuffering
                            action h)

main :: IO ()
main = net dispath
