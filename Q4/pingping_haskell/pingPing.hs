import Control.Concurrent
import System.IO

ping :: Int -> Int -> IO ()
ping 0 _ = return ()
ping n count = do
    putStrLn $ "Toma aí o Ping número: " ++ show count
    ping (n - 1) (count + 1)

pong :: Int -> Int -> IO ()
pong 0 _  = return ()
pong n count = do
    putStrLn $ "Eita! Recebi o Ping número: " ++ show count
    pong (n - 1) (count + 1)

main :: IO ()
main = do
    putStrLn "Quantas mensagens deseja enviar?"
    --recebe input do usuario
    n <- readLn

    let pingThread = ping n 1
    let pongThread = pong n 1

    forkIO pingThread
    forkIO pongThread

    threadDelay (n * 10000) 